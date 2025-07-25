package com.techreier.edrops.service

// https://www.ssb.no/energi-og-industri/energi/statistikk/elektrisitet
// https://www.norskpetroleum.no/fakta/historisk-produksjon/#arlig

const val SM3_TO_BOE = 6.29 * 1_000_000 //Sm3 = Standard cubic metre, volume measures with pressure and temperature (15°C og 1 atm)
const val BOE_TO_TWH = 0.00000612 // 1 boe = ~6.12 GJ ≈ 0.0000017 TWh

enum class EnergySource(
    val isRenewable: Boolean, // True if renewable energy source
    val energyUnit: String, // Unit of energy measurement (e.g., MJ, MWh, etc.)
    val twhPerUnit: Double, // TWh per energy unit
    val conversionEfficiency: Double, // Efficiency for converting raw energy to electricity (e.g., fossil fuels)
    val directUseEfficiency: Double, // Efficiency when used directly (e.g., heating or cooking)
    val co2Factor: Double, // kg CO2 per boe
) {
    NATURAL_GAS(false, "Sm3", SM3_TO_BOE * BOE_TO_TWH, 0.50, 1.0, 336.0),
    OIL(false, "Sm3", SM3_TO_BOE * BOE_TO_TWH, 0.37, 1.0, 445.0),
    COAL(false, "tonne", 0.00667, 0.35, 0.80, 2400.0),
    NUCLEAR(false, "TWh", 1.0, 0.33, 1.0, 0.00),

    // Renewable energy sources (no conversion needed, so set conversion efficiency to 1.0)
    SOLAR(true, "TWh", 1.0, 1.0, 1.0, 0.0), // Solar energy typically converts directly with high efficiency
    WIND(true, "TWh", 1.0, 1.0, 1.0, 0.0), // Wind energy directly produces electricity
    WATER(true, "TWh", 1.0, 1.0, 1.0, 0.0); // Hydropower (direct energy production with high efficiency)


    // Calculate electricity production in TWh (for fossil and nuclear sources)
    fun toElectricityTWh(input: Double?): Double? = input?.let {it * twhPerUnit * conversionEfficiency}

    // Calculate direct energy use (e.g., for heating or cooking)
    fun toDirectUseTWh(input: Double?): Double? =  input?.let {it * twhPerUnit * directUseEfficiency}

    fun tonnCo2PerTWh(): Double = (co2Factor / BOE_TO_TWH) / conversionEfficiency

}
