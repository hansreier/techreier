package com.techreier.edrops.dto

import com.techreier.edrops.domain.BlogEntry
import java.time.ZonedDateTime

data class BlogEntryDTO(
    val id: Long?,
    val changed: ZonedDateTime?,
    val segment: String,
    var title: String,
    val summary: String
)

fun BlogEntry.toDTO(): BlogEntryDTO {
    return BlogEntryDTO(
        id = this.id,
        changed = this.changed,
        segment = this.segment,
        title = this.title,
        summary = this.summary,
    )
}
