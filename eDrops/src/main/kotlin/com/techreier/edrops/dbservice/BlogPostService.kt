package com.techreier.edrops.dbservice

import com.techreier.edrops.config.logger
import com.techreier.edrops.domain.BlogPost
import com.techreier.edrops.domain.BlogText
import com.techreier.edrops.domain.PostState
import com.techreier.edrops.exceptions.ParentBlogException
import com.techreier.edrops.forms.BlogPostForm
import com.techreier.edrops.repository.BlogPostRepository
import com.techreier.edrops.repository.BlogRepository
import com.techreier.edrops.repository.BlogTextRepository
import org.apache.commons.lang3.StringUtils.trim
import org.springframework.dao.DuplicateKeyException
import org.springframework.dao.DataRetrievalFailureException
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.time.Instant

@Service
@Transactional
class BlogPostService(
    private val blogPostRepo: BlogPostRepository,
    private val blogRepo: BlogRepository,
    private val blogTextRepo: BlogTextRepository,
) {
    fun save(blogId: Long, blogPostForm: BlogPostForm, timestamp: Instant): Long {
        logger.info("Saving blogPost with id: ${blogPostForm.id} segment: ${blogPostForm.segment} blogId: $blogId")

        val blog = blogRepo.findById(blogId).orElse(null)?.takeIf { it.id != null }
            ?: throw ParentBlogException("Cannot use blog with id: $blogId — not found or detached")

        val blogPost =
            BlogPost(
                timestamp,
                blogPostForm.state.name,
                blogPostForm.segment,
                blogPostForm.title,
                blogPostForm.summary,
                blog,
                blogPostForm.id
            )
        val savedBlogPost = blogPostRepo.save(blogPost)
        savedBlogPost ?: throw DataRetrievalFailureException("Failed to save BlogPost: $blogPost")
        savedBlogPost.id ?: throw DataRetrievalFailureException("Failed to save BlogPost: $blogPost. No id Returned")

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
                blogTextRepo.save(BlogText(timestamp, blogPostForm.state.name,content, blogPost))
        }
        return savedBlogPost.id
    }

    fun delete(
        blogId: Long?,
        blogPostForm: BlogPostForm,
    ) {
        logger.info("Deleting blogPost with id: ${blogPostForm.id} segment: ${blogPostForm.segment} blogId: $blogId")
        blogPostForm.id?.let { id ->
            blogTextRepo.deleteById(id)
            blogPostRepo.deleteById(id)
        } ?: logger.error("BlogPost not deleted, no id")
    }

    fun readBlogPost(blogId: Long?, segment: String, admin: Boolean, id: Long? = null): Pair<BlogPost?, BlogText?> {
        logger.info("AdminRead: $admin")
        blogId?: throw ResponseStatusException(HttpStatus.NOT_FOUND,
            "Blog with no id for blogPost segment: $segment")
        val blogPost: BlogPost? = if (admin) {
            if (id != null) {
                blogPostRepo.findById(id).orElse(null)
            } else {
                val posts = blogPostRepo.findByBlogIdAndSegment(blogId, segment)
                if (posts.size > 1) {
                    throw DuplicateKeyException("Duplicate blogpost ids: " + posts.map { it.id })
                }
                posts.first()
            }
        }
        else {
            val publishedPosts = blogPostRepo.findByBlogIdAndSegment(blogId, segment, PostState.PUBLISHED.name)
            if (publishedPosts.size > 1) {
                throw DuplicateKeyException("Duplicate blogpost ids: " + publishedPosts.map { it.id })
            }
            publishedPosts.first()
        }

        val blogText = if (blogPost != null && blogPost.id != null) {
            val found = blogTextRepo.findById(blogPost.id).orElse(null)
            if (found?.id != null) found else null
        } else {
            null
        }
        return Pair(blogPost, blogText)
    }

    //Only check on duplicate if state is PUBLISHED
    fun duplicate(segment: String, blogId: Long, state: PostState, blogPostId: Long?): Boolean {
            return if (state == PostState.PUBLISHED) {
                blogPostRepo.findBlogPostIds(segment, blogId, PostState.PUBLISHED.name).any { it != blogPostId }
            } else false
    }
}
