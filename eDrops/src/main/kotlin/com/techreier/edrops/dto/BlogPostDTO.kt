package com.techreier.edrops.dto

import com.techreier.edrops.config.logger
import com.techreier.edrops.domain.BlogPost
import com.techreier.edrops.forms.BlogPostForm
import com.techreier.edrops.util.markdownToHtml
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

fun BlogPost.toDTO(zoneId: ZoneId, html: Boolean = false): BlogPostDTO {
    logger.info("Reier blogPost: $html")
    return BlogPostDTO(
        id = this.id,
        changed = this.changed?.atZone(zoneId),
        segment = this.segment,
        title = this.title,
        summary = if (html) markdownToHtml(this.summary, true) else this.summary,
    )
}
