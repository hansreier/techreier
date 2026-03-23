package com.techreier.edrops.dbservice

import com.techreier.edrops.config.logger
import com.techreier.edrops.domain.BlogPost
import com.techreier.edrops.domain.BlogText
import com.techreier.edrops.domain.PostState
import com.techreier.edrops.exceptions.KeyNotFoundException
import com.techreier.edrops.forms.BlogPostForm
import com.techreier.edrops.repository.BlogPostRepository
import com.techreier.edrops.repository.BlogRepository
import com.techreier.edrops.repository.BlogTextRepository
import org.springframework.dao.DataRetrievalFailureException
import org.springframework.dao.DuplicateKeyException
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
    fun save(blogId: Long, blogPostId: Long?, blogPostForm: BlogPostForm, timestamp: Instant): Long {
        logger.info("Saving blogPost id: ${blogPostId} segment: ${blogPostForm.segment} state: ${blogPostForm.state.name} blogId: $blogId")

        val blogProxy = blogRepo.getReferenceById(blogId)

        val blogPost =
            BlogPost(
                timestamp,
                blogPostForm.state.name,
                blogPostForm.segment,
                blogPostForm.title,
                blogPostForm.summary,
                blogProxy,
                blogPostId
            )
        val savedBlogPost: BlogPost = blogPostRepo.save(blogPost)
        savedBlogPost.id ?: throw DataRetrievalFailureException("Failed to save BlogPost: $blogPost. No id Returned")

        val blogText: BlogText? = blogPostId?.let { blogTextRepo.findById(it).orElse(null) }
        val content = blogPostForm.content.trim()
        if (blogText != null) {
            if (content.isEmpty())
                blogTextRepo.delete(blogText)
            else {
                blogText.changed = blogPost.changed
                blogText.text = content
            }
        } else {
            if (content.isNotEmpty())
                blogTextRepo.save(BlogText(timestamp, blogPostForm.state.name, content, blogPost))
        }
        return savedBlogPost.id
    }

    fun delete(
        blogId: Long?, blogPostId: Long?,
        blogPostForm: BlogPostForm,
    ) {
        logger.info("Deleting blogPost id: ${blogPostId} segment: ${blogPostForm.segment} state: ${blogPostForm.state} blogId: $blogId")
        blogPostId?.let { id ->
            blogTextRepo.deleteById(id)
            blogPostRepo.deleteById(id)
        } ?: logger.error("BlogPost not deleted, no id")
    }

    fun readBlogPost(
        blogId: Long?, segment: String,
        state: PostState = PostState.PUBLISHED,
    ): Pair<BlogPost?, BlogText?> {
        blogId ?: throw ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "Blog with no id for blogPost segment: $segment"
        )
        logger.info("Søker etter poster: state=${state.name} blogId=$blogId")
        val posts = blogPostRepo.findByBlogIdAndSegmentAndState(blogId, segment, state.name)
        if (posts.isEmpty()) {
            throw KeyNotFoundException("Blogpost not found: blogId: $blogId segment: $segment state: ${state.name}")
        }
        if (posts.size > 1) {
            throw DuplicateKeyException("Blogpost duplicate ids: blogId: $blogId ids: ${posts.map { it.id }}")
        }
        val blogPost = posts.first()

        val blogText = if (blogPost.id != null) {
            val found = blogTextRepo.findById(blogPost.id).orElse(null)
            if (found?.id != null) found else null
        } else {
            null
        }
        return Pair(blogPost, blogText)
    }

    fun findId(segment: String, blogId: Long, state: PostState): Long {
        val ids = blogPostRepo.findBlogPostIds(segment, blogId, state.name)
        if (ids.isEmpty()) {
            throw KeyNotFoundException("Blogpost not found: blogId: $blogId segment: $segment state: ${state.name}")
        }
        if (ids.size > 1) {
            throw DuplicateKeyException("Blogpost duplicate ids: blogId: $blogId ids: $ids}")
        }
        return ids.first()
    }

    fun duplicate(segment: String, blogId: Long, state: PostState, blogPostId: Long?): Boolean {
        return blogPostRepo.findBlogPostIds(segment, blogId, state.name).any { it != blogPostId }

    }
}
