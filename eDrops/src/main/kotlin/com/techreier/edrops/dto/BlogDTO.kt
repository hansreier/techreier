package com.techreier.edrops.dto

import com.techreier.edrops.domain.Blog
import com.techreier.edrops.forms.BlogForm
import java.time.ZonedDateTime

data class BlogDTO(
    val id: Long?, val topicKey: String, val topicText: String?, val langCodeFound: String, val langCodeWanted: String,
    val changed: ZonedDateTime, val segment: String, val pos: Int,
    val subject: String, val about: String, val blogPosts: List<BlogPostDTO>,
) {
    // new empty instance creation
    constructor(langCode: String) : this(
        null, "", null, langCode, langCode, ZonedDateTime.now(), "", 0, "", "", listOf<BlogPostDTO>()
    )

    fun toForm(): BlogForm {
        return BlogForm(this.id, this.segment, this.topicKey, this.pos.toString(), this.subject, this.about)
    }

}

fun Blog.toDTO(langCodeWanted: String? = null, posts: Boolean = true): BlogDTO {
    return BlogDTO(
        this.id,
        this.topic.topicKey,
        this.topic.text,
        this.topic.language.code,
        langCodeWanted ?: this.topic.language.code,
        this.changed,
        this.segment,
        this.pos,
        this.subject,
        this.about,
        if (posts) this.blogPosts.map { it.toDTO() } else emptyList()
    )
}


