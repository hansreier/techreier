package com.techreier.edrops.dto

import com.techreier.edrops.repository.projections.IBlogText
import com.techreier.edrops.util.Markdown
import com.techreier.edrops.util.text
import java.time.ZoneId
import java.time.ZonedDateTime

class BlogTextDTO(
    val text: String,
    val changed: ZonedDateTime?,
    val changedString: String
)

fun IBlogText.toDTO(zoneId: ZoneId, datePattern: String, markdown: Markdown, html: Boolean= false ): BlogTextDTO {
    val changed = this.changed.atZone(zoneId)
    return BlogTextDTO(
        text =  if (html) markdown.toHtml(this.text) else this.text,
        changed = changed,
        changedString = changed.text(datePattern),
    )
}
