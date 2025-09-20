package com.techreier.edrops.model

import com.techreier.edrops.service.EnergySource

data class EnergyValues(
    val source: EnergySource,
    val orig: Double?,
    val twh: Double?,
    val tj: Double?
)