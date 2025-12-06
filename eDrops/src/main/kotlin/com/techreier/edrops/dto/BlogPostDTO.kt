package com.techreier.edrops.dto

import com.techreier.edrops.domain.BlogPost
import com.techreier.edrops.forms.BlogPostForm
import com.techreier.edrops.util.markdownToHtml
import com.techreier.edrops.util.text
import java.time.ZoneId
import java.time.ZonedDateTime

data class BlogPostDTO(
    val id: Long?,
    val changed: ZonedDateTime?,
    val changedString: String,
    val segment: String,
    var title: String,
    val summary: String,
) {
    fun toForm(content: String?): BlogPostForm {
        return BlogPostForm(
            id = this.id,
            segment = this.segment,
            title = this.title,
            summary = this.summary,
            content = content?: ""
        )
    }
}

fun BlogPost.toDTO(zoneId: ZoneId, datePattern: String, html: Boolean = false): BlogPostDTO {
    val changed = this.changed.atZone(zoneId)
    return BlogPostDTO(
        id = this.id,
        changed = changed, changed.text(datePattern),
        segment = this.segment,
        title = this.title,
        summary = if (html) markdownToHtml(this.summary, true) else this.summary,
    )
}
