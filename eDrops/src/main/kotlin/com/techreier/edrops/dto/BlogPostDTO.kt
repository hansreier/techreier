package com.techreier.edrops.dto

import com.techreier.edrops.domain.BlogPost
import com.techreier.edrops.domain.BlogText
import com.techreier.edrops.domain.PostState
import com.techreier.edrops.forms.BlogPostForm
import com.techreier.edrops.util.Markdown
import com.techreier.edrops.util.text
import java.time.ZoneId
import java.time.ZonedDateTime

data class BlogPostDTO(
    val id: Long?,
    val idStateString: String,
    val changed: ZonedDateTime?,
    val changedString: String,
    val created: ZonedDateTime?,
    val createdString: String,
    val state: PostState,
    val segment: String,
    var title: String,
    val summary: String,
    val blogText: BlogTextDTO? = null
) {
    fun toForm(): BlogPostForm {
        return BlogPostForm(
            id = this.id,
            segment = this.segment,
            state = this.state,
            title = this.title,
            summary = this.summary,
            content = this.blogText?.text ?:""
        )
    }
}

fun BlogPost.toDTO(zoneId: ZoneId, datePattern: String, markdown: Markdown, html: Boolean = false, blogText: BlogText? = null): BlogPostDTO {
    val changed = this.changed.atZone(zoneId)
    val created = this.created.atZone(zoneId)
    val state = PostState.entries.find { it.name == this.state } ?: PostState.UNKNOWN
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
        summary = if (html) markdown.toHtml(this.summary, true) else this.summary,
        blogText = blogText?.toDTO(zoneId, datePattern, markdown, html)
    )
}
