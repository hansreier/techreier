package com.techreier.edrops.model

data class EnergyProduction(
    val year: Int,
    val water: Double? = null,
    val wind: Double? = null,
    val solar: Double? = null,
    val heat: Double? = null,
    val oil: Double? = null,
    val oilToEl: Double? = null,
    val gas: Double? = null,
    val gasToEl: Double? = null
)
