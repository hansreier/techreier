package com.techreier.edrops.service

import com.techreier.edrops.model.EnergyValues
import com.techreier.edrops.util.msg
import org.springframework.context.MessageSource

// https://www.ssb.no/energi-og-industri/energi/statistikk/elektrisitet
// https://www.norskpetroleum.no/fakta/historisk-produksjon/#arlig

private const val STD = 1.0
private const val SM3_TO_BOE =
    6.29 * 1_000_000 //Sm3 = Standard cubic metre, volume measures with pressure and temperature (15°C og 1 atm)
private const val BOE_TO_TWH = 0.00000612 // 1 boe = ~6.12 GJ ≈ 0.0000017 TWh
private const val TWH_TO_PJ = 3.6

enum class EnergySource(
    val key: String,
    val isRenewable: Boolean, // True if renewable energy source
    val energyUnit: String, // Unit of energy measurement (e.g., MJ, MWh, etc.)
    val twhPerUnit: Double?, // TWh per energy unit
    val conversionEfficiency: Double?, // Efficiency for converting raw energy to electricity (e.g., fossil fuels)
    val directUseEfficiency: Double?, // Efficiency when used directly (e.g., heating or cooking)
    val co2Factor: Double?, // kg CO2 per boe
) {

    GAS("gas", false, "MSm3", SM3_TO_BOE * BOE_TO_TWH, 0.50, 1.0, 336.0),
    OIL("oil", false, "MSm3", SM3_TO_BOE * BOE_TO_TWH, 0.37, 1.0, 445.0),
    COAL("coal", false, "tonne", 0.00667, 0.35, 0.80, 2400.0),
    FOSSIL("fossilProd", false, "Terajoule", null, null, null, null),
    NUCLEAR("nuclear", false, "TWh", 1.0, 0.33, 1.0, 0.00),

    // Renewable energy sources (no conversion needed, so set conversion efficiency to 1.0)
    SOLAR("solar", true, "TWh", STD, STD, STD, 0.0), // Solar energy typically converts directly with high efficiency
    WIND("wind", true, "TWh", STD, STD, STD, 0.0), // Wind energy directly produces electricity
    WATER("water", true, "TWh", STD, STD, STD, 0.0), // Hydropower (direct energy production with high efficiency)
    HEAT("heat", true, "TWh", STD, STD, STD, 0.0),
    EL_PROD("elProd", true, "TWh", STD, STD, STD, 0.0),
    EL_USE("elUse", true, "TWh", STD, STD, STD, 0.0),
    EL_IMPORT("elImport", true, "TWh", STD, STD, STD, 0.0),
    EL_EXPORT("elExport", true, "TWh", STD, STD, STD, 0.0);


    // Calculate electricity production in TWh (for fossil and nuclear sources)
    fun toElectricityTWh(input: Double?): Double? {
        if (input == null || twhPerUnit == null || conversionEfficiency == null) return null
        return input * twhPerUnit * conversionEfficiency
    }

    // Calculate direct energy use (e.g., for heating or cooking)
    fun toDirectUseTWh(input: Double?): Double? {
        if (input == null || twhPerUnit == null || directUseEfficiency == null) return null
        return input * twhPerUnit * directUseEfficiency
    }

    fun toEnergyPJ(input: Double?): Double? {
        if (input == null || twhPerUnit == null || directUseEfficiency == null) return null
        return input * twhPerUnit * directUseEfficiency * TWH_TO_PJ
    }

    fun values(orig: Double? = null, twh: Double? = null, pj: Double? = null) = EnergyValues(
        this, orig,
        twh ?: toElectricityTWh(orig),
        pj ?: toEnergyPJ(orig)
    )

    fun tonnCo2PerTWh(): Double? {
        if (co2Factor == null || conversionEfficiency == null) return null
        return (co2Factor / BOE_TO_TWH) / conversionEfficiency
    }

    fun name(m: MessageSource) = msg(m, "energy." + this.key)

}
