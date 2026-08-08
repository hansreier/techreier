package com.techreier.edrops.dto

import com.techreier.edrops.domain.PostState
import com.techreier.edrops.forms.BlogPostForm
import java.time.ZonedDateTime

data class BlogPostDTO(
    val id: Long?,
    val changed: ZonedDateTime?,
    val changedString: String,
    val created: ZonedDateTime?,
    val createdString: String,
    val bumped: ZonedDateTime?,
    val bumpedString: String,
    val state: PostState,
    val stateShort: String,
    val segment: String,
    var title: String,
    val summary: String,
    val blogText: BlogTextDTO? = null
) {
    fun toForm(blogSegment: String = ""): BlogPostForm {
        return BlogPostForm(
            segment = this.segment,
            blogSegment = blogSegment,
            state = this.state,
            title = this.title,
            summary = this.summary,
            content = this.blogText?.text ?:""
        )
    }
}

