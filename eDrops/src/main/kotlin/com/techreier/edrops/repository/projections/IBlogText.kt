package com.techreier.edrops.repository.projections

import java.time.Instant

interface IBlogText {
    val changed: Instant
    val state: String
    val text: String
    val id: Long?

}

