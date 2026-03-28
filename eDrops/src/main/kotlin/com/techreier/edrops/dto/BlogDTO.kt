package com.techreier.edrops.dto

import com.techreier.edrops.dbservice.BlogWithPosts
import com.techreier.edrops.forms.BlogForm
import com.techreier.edrops.util.Markdown
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
             this.segment, this.topicKey,
            this.pos.toString(), this.subject, this.about, this.blogPosts.isNotEmpty()
        )
    }
}

fun BlogWithPosts.toDTO(
    zoneId: ZoneId, datetimePattern: String, datePattern: String, markdown: Markdown, langCodeWanted: String? = null,
    posts: Boolean = true, html: Boolean = false,
): BlogDTO {
    val changed = this.blog.changed.atZone(zoneId)
    val blogPosts = if (posts) this.posts?.map {
        it.toDTO(zoneId, datePattern, markdown, html)
    } ?: emptyList()
    else emptyList()

    return BlogDTO(
        id = this.blog.id,
        topicKey = this.blog.topicKey,
        topicText = this.blog.topicText,
        langCodeFound = this.blog.languageCode,
        langCodeWanted = langCodeWanted ?: this.blog.languageCode,
        changed = changed,
        changedText = changed.text(datetimePattern),
        segment = this.blog.segment,
        pos = this.blog.pos,
        subject = this.blog.subject,
        about = if (html) markdown.toHtml(this.blog.about) else this.blog.about,
        blogPosts = blogPosts,
    )
}


