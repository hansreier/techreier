package com.techreier.edrops.dto

import java.time.ZonedDateTime

class BlogTextDTO(
    val text: String,
    val changed: ZonedDateTime?,
    val changedString: String
)
