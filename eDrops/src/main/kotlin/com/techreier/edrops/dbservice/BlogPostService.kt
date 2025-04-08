package com.techreier.edrops.dbservice

import com.techreier.edrops.config.logger
import com.techreier.edrops.domain.Blog
import com.techreier.edrops.domain.BlogPost
import com.techreier.edrops.exceptions.DuplicateSegmentException
import com.techreier.edrops.exceptions.ParentBlogException
import com.techreier.edrops.forms.BlogPostForm
import com.techreier.edrops.repository.BlogPostRepository
import com.techreier.edrops.repository.BlogRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime

@Service
@Transactional
class BlogPostService(
    private val blogPostRepo: BlogPostRepository,
    private val blogRepo: BlogRepository
) {
    fun save(
        blogId: Long?,
        blogPostForm: BlogPostForm,
    ) {
        logger.info("Saving blogPost with id: ${blogPostForm.id} segment: ${blogPostForm.segment} blogId: $blogId")
        blogId?.let {
            //TODO ?= is wrong,
            val blog: Blog? = blogRepo.findById(blogId).orElse(null)
                ?: throw ParentBlogException("BlogPost ${blogPostForm.segment} not saved, cannot read parent blog with id: $blogId")

            val blogPost =
                BlogPost(
                    ZonedDateTime.now(),
                    blogPostForm.segment,
                    blogPostForm.title,
                    blogPostForm.summary,
                    blog!!,
                    blogPostForm.id
                )
            if (blog.blogPosts.any { (it.segment == blogPostForm.segment) && (it.id != blogPostForm.id) }) {
                throw DuplicateSegmentException("Segment: ${blogPostForm.segment} is duplicate in blog ${blog.segment}")
            }

            blogPostRepo.save(blogPost)
        } ?: throw ParentBlogException("BlogPntry ${blogPostForm.segment} not saved, parent blog is detached")
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
}
