package com.techreier.edrops.service

import com.techreier.edrops.config.logger
import com.techreier.edrops.model.El
import com.techreier.edrops.model.EnergyProduction
import com.techreier.edrops.model.OilGas
import org.apache.poi.ss.usermodel.Cell
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
                    getYear(row.getCell(0)),
                    water = int(row.getCell(2)),
                    wind = int(row.getCell(3)),
                    sun = int(row.getCell(4)),
                    heat = int(row.getCell(5)),
                    export = int(row.getCell(6)),
                    import = int(row.getCell(7))
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
                    getYear(row.getCell(0)),
                    oil = double(row.getCell(1)),
                    condensate = double(row.getCell(2)),
                    ngl = double(row.getCell(3)),
                    gas = double(row.getCell(4)),
                )

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

    //Return int value from cell, if not int or empty null is returned
    fun int(cell: Cell?): Int? {
        if (cell == null) return null
        val str = formatter.formatCellValue(cell).trim()
        return str.toIntOrNull()
    }

    //Return double value from cell, if not int or empty null is returned
    fun double(cell: Cell?): Double? {
        if (cell == null) return null
        val str = formatter.formatCellValue(cell).trim()
        return str.toDoubleOrNull()
    }

    fun getYear(yearCell: Cell?): Int {
        val year = yearCell?.let { formatter.formatCellValue(yearCell).trim() }
        val yearInt: Int = year?.toIntOrNull() ?: throw (NumberFormatException("Invalid year: $year"))
        if (yearInt < 1950 || yearInt > Year.now().value) throw (NumberFormatException("Too old or future year: $yearInt"))
        return yearInt
    }

}