package com.techreier.edrops.dbservice

import com.techreier.edrops.config.logger
import com.techreier.edrops.domain.BlogPost
import com.techreier.edrops.domain.BlogText
import com.techreier.edrops.domain.PostState
import com.techreier.edrops.dto.BlogPrincipal
import com.techreier.edrops.dto.PostWithText
import com.techreier.edrops.exceptions.BlogNotFoundException
import com.techreier.edrops.exceptions.PostNotFoundException
import com.techreier.edrops.forms.BlogPostForm
import com.techreier.edrops.repository.BlogPostRepository
import com.techreier.edrops.repository.BlogRepository
import com.techreier.edrops.repository.BlogTextRepository
import com.techreier.edrops.repository.projections.IBlogPostSummary
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
    private val blogService: BlogService,
    private val blogTextRepo: BlogTextRepository,
) {
    fun save(blogPrincipal: BlogPrincipal, blogPostId: Long?, blogPostForm:
            BlogPostForm, changed: Instant, bumped: Instant? = null): Long {
        logger.info("Saving blogPost id=$blogPostId segment=${blogPostForm.segment} " +
                "state=${blogPostForm.state.name} blogPrincipal=$blogPrincipal}")

        val blogId = if (blogPostForm.blogSegment.isNotBlank()) { // Attempted to change parent blog
            blogService.findId(
                segment = blogPostForm.blogSegment,
                blogOwnerId = blogPrincipal.ownerId,
                languageCode =blogPrincipal.langCode) ?: throw (BlogNotFoundException("New blog cannot be selected"))
        } else {
            blogPrincipal.blogId
                ?: throw (BlogNotFoundException("Probably not logged in. " +
                        "BlogId not found for segment=${blogPostForm.segment} blogPrincipal=$blogPrincipal"))
        }


        val blogProxy = blogRepo.getReferenceById(blogId)

        val blogPost =
            BlogPost(
                changed = changed,
                bumped = bumped ?: changed,
                state = blogPostForm.state.name,
                segment = blogPostForm.segment,
                title = blogPostForm.title,
                summary = blogPostForm.summary,
                blog = blogProxy,
                id = blogPostId
            )
        val savedBlogPost: BlogPost = blogPostRepo.save(blogPost)
        val blogPostId = savedBlogPost.id
            ?: throw DataRetrievalFailureException("Failed to save BlogPost: $blogPost. No id Returned")

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
                blogTextRepo.save(BlogText(changed, blogPostForm.state.name, content, blogPost))
        }
        return blogPostId
    }

    fun delete(
        blogId: Long?, blogPostIds: List<Long>,
    ) {
        logger.info("Deleting blogPost ids: $blogPostIds  blogId: $blogId")
        blogPostIds.let { ids ->
            blogTextRepo.deleteAllById(ids)
            blogPostRepo.deleteAllById(ids)
        }
    }

    //TODO Add test
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
            val dupList = posts.map { it.id }
            logger.warn("Blogpost duplicate ids: blogId: $blogId ids: $dupList")
            dupList
        } else listOf()
        val blogPost = posts.first()
        val found = blogTextRepo.findPById(blogPost.id)
        val blogText = if (found?.id != null) found else null
        logger.info("BlogPost read")
        return PostWithText(blogPost, blogText, duplicates)
    }


    fun findSummaries(segment: String, blogId: Long, state: PostState): List<IBlogPostSummary> {
        return blogPostRepo.findBlogPostSummaries(segment, blogId, state.name)
    }

    // If save existing or new post (given by blogPostId), check if it will be a duplicate
    fun duplicate(segment: String, blogId: Long, state: PostState, blogPostId: Long?): Boolean {
        val summaries = blogPostRepo.findBlogPostSummaries(segment, blogId, state.name)
        return summaries.any {  it.id != blogPostId }
    }
}
