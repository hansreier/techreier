package com.techreier.edrops.dto

import com.techreier.edrops.domain.Blog
import com.techreier.edrops.forms.BlogForm
import com.techreier.edrops.util.markdownToHtml
import com.techreier.edrops.util.text
import java.time.ZoneId
import java.time.ZonedDateTime

data class BlogDTO(
    val id: Long?, val topicKey: String, val topicText: String?, val langCodeFound: String, val langCodeWanted: String,
    val changed: ZonedDateTime?, val changedText: String, val segment: String, val pos: Int,
    val subject: String, val about: String, val blogPosts: List<BlogPostDTO>,
) {
    // new empty instance creation
    constructor(langCode: String) : this(
        null, "", null,
        langCode, langCode, null, "",
        "", 0, "", "", listOf<BlogPostDTO>()
    )

    fun toForm(): BlogForm {
        return BlogForm(
            this.id, this.segment, this.topicKey,
            this.pos.toString(), this.subject, this.about, this.blogPosts.isNotEmpty()
        )
    }
}

fun Blog.toDTO(
    zoneId: ZoneId, datetimePattern: String, datePattern: String, langCodeWanted: String? = null,
    posts: Boolean = true, html: Boolean = false,
): BlogDTO {
    val changed = this.changed.atZone(zoneId)
    return BlogDTO(
        this.id,
        this.topic.topicKey,
        topicText = this.topic.text,
        langCodeFound = this.topic.language.code,
        langCodeWanted = langCodeWanted ?: this.topic.language.code,
        changed = changed,
        changedText = changed.text(datetimePattern),
        segment = this.segment,
        pos = this.pos,
        subject = this.subject,
        about = if (html) markdownToHtml(this.about, true) else this.about,
        blogPosts = if (posts) this.blogPosts.map { it.toDTO(zoneId, datePattern, html) } else emptyList()
    )
}


