package com.techreier.edrops.model

import com.techreier.edrops.service.EnergySource

data class EnergyValues(
    val source: EnergySource,
    val orig: Double? = null,
    val twh: Double? = null,
    val tj: Double? = null
)