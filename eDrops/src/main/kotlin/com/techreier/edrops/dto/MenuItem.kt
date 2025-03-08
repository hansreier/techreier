package com.techreier.edrops.dto

//TODO evaluate use of id here, no longer in use I suspect
data class MenuItem(
    val subject: String,
    val segment: String,
    val topic: String,
    val collapsible: Boolean
)
