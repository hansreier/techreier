package com.techreier.edrops.model.ssb.request

data class SsbQueryRequest(
    val query: List<QueryFilter>,
    val response: ResponseFormat
)