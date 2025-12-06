package com.techreier.edrops.dbservice

import com.techreier.edrops.config.logger
import com.techreier.edrops.domain.BlogPost
import com.techreier.edrops.domain.BlogText
import com.techreier.edrops.exceptions.ParentBlogException
import com.techreier.edrops.forms.BlogPostForm
import com.techreier.edrops.repository.BlogPostRepository
import com.techreier.edrops.repository.BlogRepository
import com.techreier.edrops.repository.BlogTextRepository
import org.apache.commons.lang3.StringUtils.trim
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
@Transactional
class BlogPostService(
    private val blogPostRepo: BlogPostRepository,
    private val blogRepo: BlogRepository,
    private val blogTextRepo: BlogTextRepository,
) {
    fun save(blogId: Long, blogPostForm: BlogPostForm, timestamp: Instant) {
        logger.info("Saving blogPost with id: ${blogPostForm.id} segment: ${blogPostForm.segment} blogId: $blogId")

        val blog = blogRepo.findById(blogId).orElse(null)?.takeIf { it.id != null }
            ?: throw ParentBlogException("Cannot use blog with id: $blogId — not found or detached")

        val blogPost =
            BlogPost(
                timestamp,
                blogPostForm.segment,
                blogPostForm.title,
                blogPostForm.summary,
                blog,
                blogPostForm.id
            )
        blogPostRepo.save(blogPost)

        val blogText: BlogText? = blogPostForm.id?.let { blogTextRepo.findById(it).orElse(null) }
        val content = trim(blogPostForm.content)
        if (blogText != null) {
            if (content.isEmpty())
                blogTextRepo.delete(blogText)
            else {
                blogText.changed = blogPost.changed
                blogText.text = content
            }
        } else {
            if (content.isNotEmpty())
                blogTextRepo.save(BlogText(timestamp, content, blogPost))
        }
    }

    fun delete(
        blogId: Long?,
        blogPostForm: BlogPostForm,
    ) {
        logger.info("Deleting blogPost with id: ${blogPostForm.id} segment: ${blogPostForm.segment} blogId: $blogId")
        blogPostForm.id?.let { id ->
            blogPostRepo.deleteById(id)
            blogTextRepo.deleteById(id)
        } ?: logger.error("BlogPost not deleted, no id")
    }

    fun getBlogText(blogPostId: Long?): BlogText? {
        val blogText = blogPostId?.let {
            blogTextRepo.findById(blogPostId).orElse(null)?.takeIf { it.id != null }
        }
        return blogText
    }

    fun duplicate(segment: String, blogId: Long, blogPostId: Long?): Boolean {
        return blogPostRepo.findBlogPostIds(segment, blogId).any { it != blogPostId }
    }
}
