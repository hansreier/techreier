package com.techreier.edrops.repository.projections

import com.techreier.edrops.domain.PostState
import com.techreier.edrops.dto.BlogPostDTO
import com.techreier.edrops.util.Markdown
import com.techreier.edrops.util.text
import java.time.Instant
import java.time.ZoneId

interface IBlogPost {
    val title: String
    val summary: String
    val state: String
    val segment: String
    val changed: Instant
    val created: Instant
    val id: Long?
}

fun IBlogPost.toDTO(
    zoneId: ZoneId,
    datePattern: String,
    markdown: Markdown,
    html: Boolean = false, blogText: IBlogText? = null
): BlogPostDTO {
    val changed = this.changed.atZone(zoneId)
    val created = this.created.atZone(zoneId)
    val state = PostState.find(this.state)
    return BlogPostDTO(
        id = this.id,
        idStateString = "${this.id.toString()} - $state" ,
        changed = changed,
        changedString = changed.text(datePattern),
        created = created,
        createdString = created.text(datePattern),
        state = state,
        segment = this.segment,
        title = this.title,
        summary = if (html) markdown.toHtml(this.summary) else this.summary,
        blogText = blogText?.toDTO(zoneId, datePattern, markdown, html)
    )
}