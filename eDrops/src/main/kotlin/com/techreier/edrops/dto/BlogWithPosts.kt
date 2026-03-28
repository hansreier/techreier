package com.techreier.edrops.dto

import com.techreier.edrops.repository.projections.IBlog
import com.techreier.edrops.repository.projections.IBlogPost
import com.techreier.edrops.repository.projections.toDTO
import com.techreier.edrops.util.Markdown
import com.techreier.edrops.util.text
import java.time.ZoneId

data class BlogWithPosts(
    val blog: IBlog,
    val posts: List<IBlogPost>?
)

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
