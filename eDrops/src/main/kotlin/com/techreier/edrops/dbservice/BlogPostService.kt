package com.techreier.edrops.dbservice

import com.techreier.edrops.config.logger
import com.techreier.edrops.domain.BlogPost
import com.techreier.edrops.domain.BlogText
import com.techreier.edrops.domain.PostState
import com.techreier.edrops.dto.PostWithText
import com.techreier.edrops.exceptions.PostNotFoundException
import com.techreier.edrops.forms.BlogPostForm
import com.techreier.edrops.repository.BlogPostRepository
import com.techreier.edrops.repository.BlogRepository
import com.techreier.edrops.repository.BlogTextRepository
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
        val blogPostId = savedBlogPost.id ?: throw DataRetrievalFailureException("Failed to save BlogPost: $blogPost. No id Returned")

        val blogText: BlogText? = blogTextRepo.findById(blogPostId).orElse(null)
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
        return blogPostId
    }

    fun delete(
        blogId: Long?, blogPostIds: List<Long>,
        blogPostForm: BlogPostForm,
    ) {
        logger.info("Deleting blogPost ids: $blogPostIds segment: ${blogPostForm.segment} state: ${blogPostForm.state} blogId: $blogId")
        blogPostIds.let { ids ->
            blogTextRepo.deleteAllById(ids)
            blogPostRepo.deleteAllById(ids)
        }
    }

    @Transactional(readOnly = true)
    fun readBlogPost(
        blogId: Long?, segment: String,
        state: PostState = PostState.PUBLISHED,
    ): PostWithText {
        blogId ?: throw ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "Blog with no id for blogPost segment: $segment"
        )
        logger.info("Søker etter poster: state=${state.name} blogId=$blogId")
        val posts = blogPostRepo.findPByBlogIdAndSegmentAndState(blogId, segment, state.name)
        if (posts.isEmpty()) {
            throw PostNotFoundException("Blogpost not found: blogId: $blogId segment: $segment state: ${state.name}")
        }
        val duplicates = if (posts.size > 1) {
            val dupList = posts.mapNotNull { it.id }
            logger.warn("Blogpost duplicate ids: blogId: $blogId ids: $dupList")
            dupList
        } else listOf()
        val blogPost = posts.first()
        val blogPostId = blogPost.id ?: throw DataRetrievalFailureException("Failed to read BlogPost: $blogPost. No id Returned")
        val found = blogTextRepo.findPById(blogPostId)
        val blogText = if (found?.id != null) found else null
        logger.info("BlogPost read")
        return PostWithText(blogPost, blogText, duplicates)
    }


    fun findIds(segment: String, blogId: Long, state: PostState):List<Long> {
        return blogPostRepo.findBlogPostIds(segment, blogId, state.name)
    }

    fun duplicate(segment: String, blogId: Long, state: PostState, blogPostId: Long?): Boolean {
        return blogPostRepo.findBlogPostIds(segment, blogId, state.name).any { it != blogPostId }

    }
}
