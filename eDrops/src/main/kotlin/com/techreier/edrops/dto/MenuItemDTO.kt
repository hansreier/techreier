package com.techreier.edrops.dto

//TODO evaluate use of attributes and if a second DTO is needed
data class MenuItemDTO(
    val langCode: String,
    val subject: String,
    val segment: String,
    val topic: String,
)
