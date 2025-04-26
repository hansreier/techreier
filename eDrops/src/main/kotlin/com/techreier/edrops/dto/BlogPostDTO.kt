package com.techreier.edrops.dto

import com.techreier.edrops.domain.BlogPost
import com.techreier.edrops.forms.BlogPostForm
import java.time.ZoneId
import java.time.ZonedDateTime

data class BlogPostDTO(
    val id: Long?,
    val changed: ZonedDateTime?,
    val segment: String,
    var title: String,
    val summary: String,
) {
    fun toForm(): BlogPostForm {
        return BlogPostForm(
            id = this.id,
            segment = this.segment,
            title = this.title,
            summary = this.summary
        )
    }
}

fun BlogPost.toDTO(zoneId: ZoneId): BlogPostDTO {
    return BlogPostDTO(
        id = this.id,
        changed = this.changed?.atZone(zoneId),
        segment = this.segment,
        title = this.title,
        summary = this.summary,
    )
}
