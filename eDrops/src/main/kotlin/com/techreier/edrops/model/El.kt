package com.techreier.edrops.model

data class El(
    val year: Int,
    val water: Double?,
    val wind: Double?,
    val solar: Double?,
    val heat: Double?,
    val import: Double?,
    val export: Double?,
)
