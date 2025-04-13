package com.techreier.edrops.dbservice

import com.techreier.edrops.config.logger
import com.techreier.edrops.domain.Blog
import com.techreier.edrops.domain.BlogPost
import com.techreier.edrops.forms.BlogPostForm
import com.techreier.edrops.repository.BlogPostRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime

@Service
@Transactional
class BlogPostService(
    private val blogPostRepo: BlogPostRepository,
) {
    fun save(
        blog: Blog,
        blogPostForm: BlogPostForm,
    ) {
        logger.info("Saving blogPost with id: ${blogPostForm.id} segment: ${blogPostForm.segment} blogId: $blog.blogId")

        val blogPost =
            BlogPost(
                ZonedDateTime.now(),
                blogPostForm.segment,
                blogPostForm.title,
                blogPostForm.summary,
                blog,
                blogPostForm.id
            )

        blogPostRepo.save(blogPost)
    }

    fun delete(
        blogId: Long?,
        blogPostForm: BlogPostForm,
    ) {
        logger.info("Deleting blogPost with id: ${blogPostForm.id} segment: ${blogPostForm.segment} blogId: $blogId")
        blogPostForm.id?.let { id ->
            blogPostRepo.deleteById(id)
        } ?: logger.error("BlogPost not deleted, no id")
    }

    fun duplicate(segment: String, blogId: Long, blogPostId: Long?): Boolean {
        return blogPostRepo.findBlogPostIds(segment, blogId).any { it != blogPostId }
    }
}
