package com.techreier.edrops.model

import com.techreier.edrops.service.EnergySource

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

data class EnergyYear(
    val year: Int,
    val source: MutableList<EnergyValues>
)

data class EnergyValues(
    val source: EnergySource,
    val orig: Double?,
    val twh: Double?,
    val tj: Double?
)