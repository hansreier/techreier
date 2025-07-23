package com.techreier.edrops.model

data class OilGas(
    val year: Int,
    val oil: Double?,
    val condensate: Double?,
    val ngl: Double?,
    val gas: Double?
)