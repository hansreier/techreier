package com.techreier.edrops.dto

import com.techreier.edrops.config.logger
import com.techreier.edrops.domain.Blog
import com.techreier.edrops.forms.BlogForm
import com.techreier.edrops.util.markdownToHtml
import java.time.ZoneId
import java.time.ZonedDateTime

data class BlogDTO(
    val id: Long?, val topicKey: String, val topicText: String?, val langCodeFound: String, val langCodeWanted: String,
    val changed: ZonedDateTime?, val segment: String, val pos: Int,
    val subject: String, val about: String, val blogPosts: List<BlogPostDTO>,
) {
    // new empty instance creation
    constructor(langCode: String) : this(
        null, "", null, langCode, langCode, null, "", 0, "", "", listOf<BlogPostDTO>() //ZonedDateTime.now()
    )

    fun toForm(): BlogForm {
        return BlogForm(this.id, this.segment, this.topicKey,
            this.pos.toString(), this.subject, this.about, this.blogPosts.isNotEmpty()
        )
    }
}

fun Blog.toDTO(zoneId: ZoneId, langCodeWanted: String? = null, posts: Boolean = true, html: Boolean = false): BlogDTO {
    logger.info("Reier blog: $html")
    return BlogDTO(
        this.id,
        this.topic.topicKey,
        this.topic.text,
        this.topic.language.code,
        langCodeWanted ?: this.topic.language.code,
        this.changed.atZone(zoneId),
        this.segment,
        this.pos,
        this.subject,
        if (html) markdownToHtml(this.about, true) else this.about,
        if (posts) this.blogPosts.map { it.toDTO(zoneId, html) } else emptyList()
    )
}


