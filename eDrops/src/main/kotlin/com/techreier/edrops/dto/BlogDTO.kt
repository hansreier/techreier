package com.techreier.edrops.dto

import com.techreier.edrops.domain.Blog
import com.techreier.edrops.forms.BlogForm
import java.time.ZonedDateTime

data class BlogDTO(
    val id: Long?, val topicKey: String, val topicText: String?, val langCodeFound: String, val langCodeWanted: String,
    val changed: ZonedDateTime, val segment: String,
    val subject: String, val about: String, val blogEntries: List<BlogEntryDTO>,
) {
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

fun Blog.toDTO(langCodeWanted: String? = null, entries: Boolean = true): BlogDTO {
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
        blogEntries = if (entries) this.blogEntries.map { it.toDTO() } else emptyList<BlogEntryDTO>()
    )
}


