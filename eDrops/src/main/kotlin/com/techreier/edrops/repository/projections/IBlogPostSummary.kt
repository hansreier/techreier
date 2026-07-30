package com.techreier.edrops.repository.projections

import java.time.Instant

interface IBlogPostSummary {
    val id: Long
    val changed: Instant
}

