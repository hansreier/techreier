package com.techreier.edrops.dto

//TODO evaluate use of id here, no longer in use I suspect
data class MenuItemDTO(
    val id: Long,
    val subject: String,
    val segment: String,
    val topic: String,
)
