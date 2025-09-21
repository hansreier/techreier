package com.techreier.edrops.service

import com.techreier.edrops.config.logger
import com.techreier.edrops.model.El
import com.techreier.edrops.model.EnergyValues
import com.techreier.edrops.model.OilGas
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.DataFormatter
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.springframework.stereotype.Service
import java.io.FileNotFoundException
import java.time.Year

const val SSB_FILE = "SsbEl.xlsx"
const val NP_FILE = "NorskPetroleum.xlsx"
const val ENERGY_DIR = "static/energyData/"
const val SSB_FILENAME = ENERGY_DIR + SSB_FILE
const val NP_FILENAME = ENERGY_DIR + NP_FILE

// https://www.ssb.no/energi-og-industri/energi/statistikk/elektrisitet
// https://www.norskpetroleum.no/fakta/historisk-produksjon/#arlig

@Service
class EnergyService {
    val energyYears = mutableMapOf<Int, MutableList<EnergyValues>>()
    val formatter = DataFormatter()
    var error = false

    init {
        readElExcel()
        readFossilExcel()
        if (error) {
            energyYears.clear()
        }
    }

    fun readElExcel() {
        try {
            val classLoader = object {}.javaClass.classLoader
            val inputStream = classLoader.getResourceAsStream(SSB_FILENAME)

            inputStream?.use { fis ->
                WorkbookFactory.create(fis).use { workbook ->
                    val sheet = workbook.getSheetAt(0)
                    for (row in sheet.drop(1)) {
                        val el = El(
                            year = row.getCell(0).asYear(),
                            water = twh(row.getCell(2).asInt()),
                            wind = twh(row.getCell(3).asInt()),
                            solar = twh(row.getCell(4).asInt()),
                            heat = twh(row.getCell(5).asInt()),
                            export = twh(row.getCell(6).asInt()),
                            import = twh(row.getCell(7).asInt()),
                            use = twh(row.getCell(8).asInt())
                        )

                        val elProdTotal = (el.water ?: 0.0) + (el.wind ?: 0.0) + (el.solar ?: 0.0) + (el.heat ?: 0.0)

                        val energyValues = listOf(
                            EnergySource.WATER.values(el.water),
                            EnergySource.WIND.values(el.wind),
                            EnergySource.SOLAR.values(el.solar),
                            EnergySource.HEAT.values(el.heat),
                            EnergySource.EL.values(elProdTotal)
                        )

                        val energyYear = energyYears[el.year]
                        val newEnergyValues = energyYear?: mutableListOf()
                        newEnergyValues.addAll(energyValues)

                        energyYears[el.year] = energyYear ?: newEnergyValues
                    }
                }

            } ?: throw (FileNotFoundException())
            logger.info("SSB Excel sheet read successfully from: $SSB_FILENAME")

        } catch (ex: Exception) {
            logger.error("Error reading SSB  Excel Sheet $SSB_FILENAME", ex)
            error = true
        }
    }

    fun readFossilExcel() {
        try {
            val classLoader = object {}.javaClass.classLoader
            val inputStream = classLoader.getResourceAsStream(NP_FILENAME)

            inputStream?.use { fis ->
                WorkbookFactory.create(fis).use { workbook ->
                    val sheet = workbook.getSheetAt(0)

                    for (row in sheet.drop(3)) {

                        val fossil = OilGas(
                            year = row.getCell(0).asYear(),
                            oil = row.getCell(1).asDouble(),
                            condensate = row.getCell(2).asDouble(),
                            ngl = row.getCell(3).asDouble(),
                            gas = row.getCell(4).asDouble(),
                        )

                        var fossilTotalOil: Double? = null
                        if ((fossil.oil != null) || (fossil.ngl != null) || (fossil.gas != null)) {
                            fossilTotalOil = (fossil.oil ?: 0.0) + (fossil.condensate ?: 0.0) + (fossil.ngl ?: 0.0)
                        }

                        val fossilTotalTJ = (EnergySource.OIL.toEnergyTJ(fossilTotalOil) ?: 0.0) +
                                (EnergySource.GAS.toEnergyTJ(fossil.gas) ?: 0.0)

                        val energyValues = listOf(
                            EnergySource.GAS.values(fossil.gas),
                            EnergySource.OIL.values(fossilTotalOil),
                            EnergySource.FOSSIL.values(tj = fossilTotalTJ)
                        )

                        val energyYear = energyYears[fossil.year]
                        val newEnergyValues = energyYear?: mutableListOf()
                        newEnergyValues.addAll(energyValues)

                        energyYears[fossil.year] = energyYear ?: newEnergyValues

                    }
                }

            } ?: throw (FileNotFoundException())
            logger.info("Norwegian Petroleum Excel sheet read successfully from: $NP_FILENAME")

        } catch (ex: Exception) {
            logger.error("Error reading Norwegion Petroleum Excel Sheet $NP_FILENAME", ex)
            error = true
        }
    }

    private fun twh(gwh: Int?): Double? = gwh?.toDouble()?.div(1000)

    fun Cell?.asInt(): Int? {
        return this?.let {
            val str = formatter.formatCellValue(it).trim()
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