package com.techreier.edrops.dbservice

import com.techreier.edrops.config.BLOG_PUBLISHED_MIN_VALUE
import com.techreier.edrops.config.NEW_SEGMENT
import com.techreier.edrops.config.logger
import com.techreier.edrops.domain.Blog
import com.techreier.edrops.domain.PostState
import com.techreier.edrops.dto.BlogPrincipal
import com.techreier.edrops.dto.BlogWithPosts
import com.techreier.edrops.dto.MenuItem
import com.techreier.edrops.exceptions.BlogNotFoundException
import com.techreier.edrops.exceptions.DuplicateBlogException
import com.techreier.edrops.forms.BlogForm
import com.techreier.edrops.repository.BlogOwnerRepository
import com.techreier.edrops.repository.BlogPostRepository
import com.techreier.edrops.repository.BlogRepository
import com.techreier.edrops.repository.TopicRepository
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.time.Instant

@Service
@Transactional
class BlogService(
    private val ownerRepo: BlogOwnerRepository,
    private val blogRepo: BlogRepository,
    private val blogPostRepo: BlogPostRepository,
    private val topicRepo: TopicRepository,
) {

    // Read current blog based on segment,language code. Assumption: One owner
    @Transactional(readOnly = true)
    fun readBlog(
        segment: String,
        oldLangCode: String?,
        langCode: String,
        posts: Boolean = false,
        adminMenu: Boolean = false,
    ): BlogWithPosts? {
        logger.info("Read blog old LangCode: $oldLangCode langCode: $langCode, segment $segment, posts? $posts")
        val minValue = if (adminMenu) Int.MIN_VALUE else BLOG_PUBLISHED_MIN_VALUE

        val blogId = readBlogId(segment, langCode, minValue) ?: if ((oldLangCode != null) && (oldLangCode != langCode))
            readBlogId(segment, oldLangCode, minValue) ?: return null
        else return null

        val blog = blogRepo.findPById(blogId) ?: return null
        if (!posts) return BlogWithPosts(blog, null)

        val blogPosts =
            if (adminMenu) {
                val sort = Sort.by(Sort.Direction.DESC, "changed")
                blogPostRepo.findByBlogId(blogId, sort)
            } else {
                val sort = Sort.by(Sort.Direction.DESC, "changed")
                blogPostRepo.findByBlogIdAndState(blogId, PostState.PUBLISHED.name, sort)
            }
        return BlogWithPosts(blog, blogPosts)
    }

    private fun readBlogId(segment: String, langCode: String, minValue: Int): Long? {
        val blogIds: List<Long> = blogRepo.findIdBySegmentAndTopicLanguageCode(segment, langCode, minValue)
        if (blogIds.size > 1)
            throw DuplicateBlogException("Duplicate blog: Segment=$segment langCode: $langCode ids: ${blogIds.map { it }}")
        return blogIds.firstOrNull()
    }

    fun readMenu(languageCode: String, adminMenu: Boolean): List<MenuItem> {
        val minValue = if (adminMenu) Int.MIN_VALUE else BLOG_PUBLISHED_MIN_VALUE
        return blogRepo.getMenuItems(languageCode, minValue)
    }

    fun save(blogPrincipal: BlogPrincipal, blogForm: BlogForm, timestamp: Instant) {
        val blogId = blogPrincipal.blogId
        val blogOwnerId = blogPrincipal.ownerId
        val langCode = blogPrincipal.langCode
        val blogOwner = ownerRepo.findById(blogOwnerId).orElse(null)
            ?: throw BlogNotFoundException("BlogOwner with id=$blogOwnerId not found")
        val id = if (blogId == -1L) null else blogId
        logger.info("Saving blog with id: $id segment: ${blogForm.segment}")
        val blog: Blog =
            id?.let {
                val foundBlog: Blog = blogRepo.findById(it).orElse(null)
                    ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Blog with id $it not found")
                if ((foundBlog.topic.topicKey != blogForm.topicKey) || foundBlog.topic.language.code != langCode) {
                    topicRepo.findByTopicKeyAndLanguageCode(blogForm.topicKey, langCode)
                        ?.let { topic -> foundBlog.topic = topic }
                        ?: logger.warn("Topic with key: ${blogForm.topicKey} and languageCode: $langCode not found")
                }
                foundBlog.changed = timestamp
                foundBlog.segment = blogForm.segment
                foundBlog.pos = blogForm.position.toIntOrNull() ?: 0
                foundBlog.subject = blogForm.subject
                foundBlog.about = blogForm.about
                foundBlog
            } ?: Blog(
                timestamp,
                blogForm.segment,
                topicRepo.findByTopicKeyAndLanguageCode(blogForm.topicKey, langCode)
                    ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Topic ${blogForm.topicKey} not found"),
                blogForm.position.toIntOrNull() ?: 0,
                blogForm.subject,
                blogForm.about,
                mutableListOf(), blogOwner
            )

        blogRepo.save(blog)
    }

    fun delete(blogId: Long?, blogForm: BlogForm) {
        if ((blogId == null) || (blogId == -1L)) {
            logger.warn("Deleting blog rejected, no valid id")
        } else {
            logger.info("Deleting blog with id: $blogId segment: ${blogForm.segment}")
            blogRepo.deleteById(blogId)
        }
    }

    fun findId(segment: String, blogOwnerId: Long, languageCode: String): Long? {
        if (segment == NEW_SEGMENT) return null
        val ids = blogRepo.findBlogIds(segment, blogOwnerId, languageCode)
        if (ids.isEmpty()) {
            throw BlogNotFoundException("Blog not found: ownerId: $blogOwnerId segment: $segment languageCode: $languageCode")
        }
        if (ids.size > 1) {
            throw DuplicateBlogException("Blog duplicate ids: ownerId: $blogOwnerId ids: $ids}")
        }
        return ids.first()
    }

    fun exists(segment: String, blogPrincipal: BlogPrincipal): Boolean {
        return blogRepo.findBlogIds(
            segment = segment,
            blogOwnerId = blogPrincipal.ownerId,
            languageCode = blogPrincipal.langCode
        ).isNotEmpty()
    }

}

