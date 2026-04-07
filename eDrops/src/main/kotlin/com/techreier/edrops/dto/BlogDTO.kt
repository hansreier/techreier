package com.techreier.edrops.dto

import com.techreier.edrops.forms.BlogForm
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
            this.pos.toString(), this.subject, this.about, true
        )
    }
}



