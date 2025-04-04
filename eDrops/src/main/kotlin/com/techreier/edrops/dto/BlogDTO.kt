package com.techreier.edrops.dto

import com.techreier.edrops.domain.Blog
import com.techreier.edrops.forms.BlogForm
import java.time.ZonedDateTime

data class BlogDTO(
    val id: Long?, val topicKey: String, val topicText: String?, val langCodeFound: String, val langCodeWanted: String,
    val changed: ZonedDateTime, val segment: String,
    val subject: String, val about: String, val blogPosts: List<BlogPostDTO>,
) {
    // new empty instance creation
    constructor(langCode: String) : this(
        null, "", null, langCode, langCode, ZonedDateTime.now(), "", "", "", listOf<BlogPostDTO>()
    )

    fun toForm(): BlogForm {
        return BlogForm(
            id = this.id,
            segment = this.segment,
            topicKey = this.topicKey,
            subject = this.subject,
            about = this.about
        )
    }

}

fun Blog.toDTO(langCodeWanted: String? = null, posts: Boolean = true): BlogDTO {
    return BlogDTO(
        id = this.id,
        topicKey = this.topic.topicKey,
        topicText = this.topic.text,
        langCodeFound = this.topic.language.code,
        langCodeWanted = langCodeWanted ?: this.topic.language.code,
        changed = this.changed,
        segment = this.segment,
        subject = this.subject,
        about = this.about,
        blogPosts = if (posts) this.blogPosts.map { it.toDTO() } else emptyList<BlogPostDTO>()
    )
}


