package com.techreier.edrops.service


enum class EnergySource(
    val isRenewable: Boolean, // True if renewable energy source
    val c02Factor: Double, // CO2 emission factor (kg CO2 per kWh of energy produced)
    val energyUnit: String, // Unit of energy measurement (e.g., MJ, MWh, etc.)
    val energyContentPerUnit: Double, // Energy per unit (e.g., MJ per kg or MWh per unit)
    val conversionEfficiency: Double?, // Efficiency for converting raw energy to electricity (e.g., fossil fuels)
    val directUseEfficiency: Double // Efficiency when used directly (e.g., heating or cooking)
) {

    // Fossil and nuclear energy sources (with conversion efficiency)
    COAL(false, 0.92, "MJ", 24_000.0, 0.35, 0.85), // 24,000 MJ per kg of coal, 35% efficiency
    NATURAL_GAS(false, 0.92, "MJ", 50_000.0, 0.40, 0.75), // Energy content of natural gas, conversion efficiency, direct use efficiency
    NUCLEAR(false, 0.0, "MJ", 24_000_000.0, 0.33, 0.80), // Energy from nuclear fuel (per kg uranium), 33% efficiency
    OIL(false, 0.92, "MJ", 42_000.0, 0.37, 0.80), // Energy content of oil, conversion efficiency, and direct use efficiency

    // Renewable energy sources (no conversion needed, so set conversion efficiency to 1.0)
    SOLAR(true, 0.0, "MJ", 1.0, 1.0, 0.85), // Solar energy typically converts directly with high efficiency
    WIND(true, 0.0, "MJ", 1.0, 1.0, 0.90), // Wind energy directly produces electricity
    WATER(true, 0.0, "MJ", 1.0, 1.0, 0.90); // Hydropower (direct energy production with high efficiency)

    // Calculate electricity production in TWh (for fossil and nuclear sources)
    fun calculateElectricEnergy(input: Double): Double {
        return if (conversionEfficiency != null) {
            // For fossil and nuclear: Energy output depends on the conversion efficiency
            input * energyContentPerUnit * conversionEfficiency / 1_000_000_000.0 // Convert to TWh
        } else {
            // For renewables: Energy output is directly available (no conversion required)
            input * energyContentPerUnit / 1_000_000_000.0 // Convert to TWh (assuming direct output)
        }
    }

    // Calculate direct energy use (e.g., for heating or cooking)
    fun calculateDirectEnergy(input: Double): Double {
        return input * energyContentPerUnit * directUseEfficiency
    }

    // Calculate CO2 emissions for the given input (in MJ), considering the CO2 factor in kg CO2 per kWh
    fun calculateCO2Emissions(input: Double): Double {
        // Convert MJ to kWh (1 kWh = 3.6 MJ)
        val energyInKWh = input / 3.6
        return energyInKWh * c02Factor
    }
}

//TODO NOT COMPLETED AT ALL
fun  xx() {
    val coalEnergyInKg = 1_000_000.0 // 1 million kg of coal
    val solarEnergyInTWh = 5.0 // 5 TWh from solar energy
    val windEnergyInTWh = 10.0 // 10 TWh from wind energy

    // Calculate electricity output for coal
    val coalElectricity = EnergySource.COAL.calculateElectricEnergy(coalEnergyInKg)
    println("Coal electricity output: $coalElectricity TWh")

    // Calculate direct energy use for solar
    val solarDirectEnergy = EnergySource.SOLAR.calculateDirectEnergy(solarEnergyInTWh)
    println("Solar direct energy use: $solarDirectEnergy MJ")

    // Calculate CO2 emissions for coal (example with MJ input)
    val coalCO2Emissions = EnergySource.COAL.calculateCO2Emissions(coalEnergyInKg * EnergySource.COAL.energyContentPerUnit)
    println("Coal CO2 emissions: $coalCO2Emissions kg CO2")

    // For renewables like wind, you can calculate the direct output as well
    val windElectricity = EnergySource.WIND.calculateElectricEnergy(windEnergyInTWh)
    println("Wind electricity output: $windElectricity TWh")
}