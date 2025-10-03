package com.techreier.edrops.model.ssb.request

data class Selection(
    val filter: String,
    val values: List<String>
)