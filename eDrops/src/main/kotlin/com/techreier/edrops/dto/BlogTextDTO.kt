package com.techreier.edrops.dto

import com.techreier.edrops.domain.BlogText
import com.techreier.edrops.util.markdownToHtml
import com.techreier.edrops.util.text
import java.time.ZoneId
import java.time.ZonedDateTime

class BlogTextDTO(
    val text: String,
    val changed: ZonedDateTime?,
    val changedString: String,

)

fun BlogText.toDTO(zoneId: ZoneId, datePattern: String, html: Boolean= false ): BlogTextDTO {
    val changed = this.changed.atZone(zoneId)
    return BlogTextDTO(
        text =  if (html) markdownToHtml(this.text, true) else this.text,
        changed = changed,
        changedString = changed.text(datePattern),
    )
}
