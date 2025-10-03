package com.techreier.edrops.model.ssb.request

data class QueryFilter(
    val code: String,
    val selection: Selection
)