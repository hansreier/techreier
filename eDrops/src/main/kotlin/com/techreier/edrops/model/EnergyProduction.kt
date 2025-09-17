package com.techreier.edrops.model

data class EnergyProduction(
    val year: Int,
    val waterTWh: Double? = null,
    val waterTJ: Double? = null,
    val windTWh: Double? = null,
    val windTJ: Double? = null,
    val solarTWh: Double? = null,
    val solarTJ: Double? = null,
    val heatTWh: Double? = null,
    val heatTJ: Double? = null,
    val elProdTWh: Double? = null,
    val elProdTJ: Double? = null,
    val oilMSm3: Double? = null,
    val oilTWh: Double? = null,
    val oilTJ: Double? = null,
    val gasMSm3: Double? = null,
    val gasTWh: Double? = null,
    val gasTJ: Double? = null
)
