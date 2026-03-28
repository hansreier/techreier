package com.techreier.edrops.dto

import com.techreier.edrops.domain.PostState
import com.techreier.edrops.forms.BlogPostForm
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
            segment = this.segment,
            state = this.state,
            title = this.title,
            summary = this.summary,
            content = this.blogText?.text ?:""
        )
    }
}

