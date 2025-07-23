package com.techreier.edrops.service


// https://www.ssb.no/energi-og-industri/energi/statistikk/elektrisitet
// https://www.norskpetroleum.no/fakta/historisk-produksjon/#arlig

const val BOE_TO_TWH = 0.0000017 // 1 boe = ~6.12 GJ ≈ 0.0000017 TWh

enum class EnergySource(
    val isRenewable: Boolean, // True if renewable energy source
    val energyUnit: String, // Unit of energy measurement (e.g., MJ, MWh, etc.)
    val twhPerUnit: Double, // TWh per energy unit
    val conversionEfficiency: Double, // Efficiency for converting raw energy to electricity (e.g., fossil fuels)
    val directUseEfficiency: Double, // Efficiency when used directly (e.g., heating or cooking)
    val co2Factor: Double, // kg CO2 per boe
) {
    NATURAL_GAS(false, "boe", BOE_TO_TWH, 50_000.0, 0.40, 310.0), //Barrel of oil equivalent
    OIL(false, "boe", BOE_TO_TWH, 42_000.0, 0.37, 430.0),
    //   COAL(false, 0.92, "MJ", 24_000.0, 0.35),
    //  NUCLEAR(false, 0.0, "MJ", 24_000_000.0, 0.33),

    // Renewable energy sources (no conversion needed, so set conversion efficiency to 1.0)
    SOLAR(true, "TWh", 1.0, 1.0, 1.0, 0.0), // Solar energy typically converts directly with high efficiency
    WIND(true, "TWh", 1.0, 1.0, 1.0, 0.0), // Wind energy directly produces electricity
    WATER(true, "TWh", 1.0, 1.0, 1.0, 0.0); // Hydropower (direct energy production with high efficiency)


    // Calculate electricity production in TWh (for fossil and nuclear sources)
    fun toElectricityTWh(input: Double?): Double? = input?.let {it * twhPerUnit * conversionEfficiency}

    // Calculate direct energy use (e.g., for heating or cooking)
    fun toDirectUseTWh(input: Double?): Double? =  input?.let {it * twhPerUnit * conversionEfficiency}

    fun tonnCo2PerTWh(): Double = (co2Factor / BOE_TO_TWH) / conversionEfficiency

}
