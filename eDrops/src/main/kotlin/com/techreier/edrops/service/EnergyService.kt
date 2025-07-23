package com.techreier.edrops.service

import com.techreier.edrops.config.logger
import com.techreier.edrops.model.El
import com.techreier.edrops.model.EnergyProduction
import com.techreier.edrops.model.OilGas
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.DataFormatter
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.springframework.stereotype.Service
import java.time.Year

const val SSB_FILE = "SsbEl.xlsx"
const val NP_FILE = "NorskPetroleum.xlsx"
const val ENERGY_DIR = "static/energydata/"
const val SSB_FILENAME = ENERGY_DIR + SSB_FILE
const val NP_FILENAME = ENERGY_DIR + NP_FILE

// https://www.ssb.no/energi-og-industri/energi/statistikk/elektrisitet
// https://www.norskpetroleum.no/fakta/historisk-produksjon/#arlig

@Service
class EnergyService {
    val energyProduction = mutableMapOf<Int, EnergyProduction>()
    val formatter = DataFormatter()

    fun readElExcel() {

        val classLoader = object {}.javaClass.classLoader
        val inputStream = classLoader.getResourceAsStream(SSB_FILENAME)

        inputStream?.use { fis ->
            val workbook = WorkbookFactory.create(fis)
            val sheet = workbook.getSheetAt(0)

            for (row in sheet.drop(1)) {
                val el = El(
                    year = row.getCell(0).asYear(),
                    water = row.getCell(2).asInt(),
                    wind = row.getCell(3).asInt(),
                    sun =  row.getCell(4).asInt(),
                    heat = row.getCell(5).asInt(),
                    export = row.getCell(6).asInt(),
                    import = row.getCell(7).asInt()
                )

                val eProdOld = energyProduction[el.year]

                val elProd = EnergyProduction(
                    el.year, twh(el.water), twh(el.wind), twh(el.sun), twh(el.heat),
                    eProdOld?.oil ?: 0.0, eProdOld?.gas ?: 0.0
                )
                energyProduction[el.year] = elProd
            }

        } ?: logger.error("failed to open Excel Sheet from file: $SSB_FILENAME")
    }

    fun readFossilExcel() {

        val classLoader = object {}.javaClass.classLoader
        val inputStream = classLoader.getResourceAsStream(NP_FILENAME)

        inputStream?.use { fis ->
            val workbook = WorkbookFactory.create(fis)
            val sheet = workbook.getSheetAt(0)

            for (row in sheet.drop(3)) {
                val fossil = OilGas(
                    year = row.getCell(0).asYear(),
                    oil = row.getCell(1).asDouble(),
                    condensate = row.getCell(2).asDouble(),
                    ngl = row.getCell(3).asDouble(),
                    gas = row.getCell(4).asDouble(),
                )
                logger.info("Fossil: $fossil")
                val eProdOld = energyProduction[fossil.year]
                var fossilTotal: Double? = null
                if ((fossil.oil != null) || (fossil.ngl != null) || (fossil.gas != null)) {
                    fossilTotal = (fossil.oil ?: 0.0) + (fossil.condensate ?: 0.0) + (fossil.ngl ?: 0.0)
                }
                val fossilProd = EnergyProduction(
                    fossil.year, eProdOld?.water, eProdOld?.wind, eProdOld?.sun, eProdOld?.heat,
                    EnergySource.OIL.toElectricityTWh(fossilTotal),
                    EnergySource.NATURAL_GAS.toElectricityTWh(fossil.gas)
                )
                energyProduction[fossil.year] = fossilProd
            }

        } ?: logger.error("failed to open Excel Sheet from file: $SSB_FILENAME")
    }

    private fun twh(gwh: Int?): Double? = gwh?.toDouble()?.div(1000)

    fun Cell?.asInt(): Int? {
        return this?.let {
            val str = DataFormatter().formatCellValue(it).trim()
            str.toIntOrNull()
        }
    }

    fun Cell?.asDouble(): Double? {
        return when {
            this == null -> null
            this.cellType == CellType.NUMERIC -> this.numericCellValue
            else -> this.toString().replace(",", "").toDoubleOrNull()
        }
    }

    fun Cell?.asYear(): Int {
        val year = this?.let { formatter.formatCellValue(this).trim() }
        val yearInt: Int = year?.toIntOrNull() ?: throw (NumberFormatException("Invalid year: $year"))
        if (yearInt < 1950 || yearInt > Year.now().value) throw (NumberFormatException("Too old or future year: $yearInt"))
        return yearInt
    }

}