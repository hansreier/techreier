package com.techreier.edrops.repository.projections

import com.techreier.edrops.dto.BlogTextDTO
import com.techreier.edrops.util.Markdown
import com.techreier.edrops.util.text
import java.time.Instant
import java.time.ZoneId

interface IBlogText {
    val changed: Instant
    val state: String
    val text: String
    val id: Long?
}

fun IBlogText.toDTO(zoneId: ZoneId, datePattern: String, markdown: Markdown, html: Boolean= false ): BlogTextDTO {
    val changed = this.changed.atZone(zoneId)
    return BlogTextDTO(
        text =  if (html) markdown.toHtml(this.text) else this.text,
        changed = changed,
        changedString = changed.text(datePattern),
    )
}

