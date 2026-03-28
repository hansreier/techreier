package com.techreier.edrops.repository.projections

import java.time.Instant

interface IBlogPost {
    val title: String
    val summary: String
    val state: String
    val segment: String
    val changed: Instant
    val created: Instant
    val id: Long?
}