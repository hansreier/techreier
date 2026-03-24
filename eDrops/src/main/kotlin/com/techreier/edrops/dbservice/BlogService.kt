package com.techreier.edrops.dbservice

import com.techreier.edrops.config.logger
import com.techreier.edrops.controllers.NEW_SEGMENT
import com.techreier.edrops.domain.Blog
import com.techreier.edrops.domain.BlogOwner
import com.techreier.edrops.domain.BlogPost
import com.techreier.edrops.dto.MenuItem
import com.techreier.edrops.exceptions.KeyNotFoundException
import com.techreier.edrops.forms.BlogForm
import com.techreier.edrops.repository.BlogRepository
import com.techreier.edrops.repository.TopicRepository
import org.springframework.dao.DuplicateKeyException
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.time.Instant

@Service
@Transactional
class BlogService(
    private val blogRepo: BlogRepository,
    private val topicRepo: TopicRepository,
) {

    // Read current blog based on segment,language code. Assumption: One owner
    @Transactional(readOnly = true)
    fun readBlog(
        segment: String,
        oldLangCode: String?,
        langCode: String,
        posts: Boolean = false,
        admin: Boolean = false,
    ): Blog? {
        logger.info("Read blog old LangCode: $oldLangCode langCode: $langCode, segment $segment, posts? $posts")

        // If blog is not found with current language, use the previous language code if different
        // This prevents annoying use of error page or redirect to home page, can fail if e.g. expired session.
        val blogLanguageDTO =
            blogRepo.getBlogWithLanguageCode(segment, langCode)
                ?: (if (oldLangCode != null && oldLangCode != langCode) blogRepo.getBlogWithLanguageCode(
                    segment,
                    oldLangCode
                ) else null)
                ?: return null

        val blog: Blog = (
                if (posts) {
                    if (admin)
                        blogRepo.findWithPostsById(blogLanguageDTO.id).orElse(null)
                    else { /* The code does not work yet
                        val blog : Blog = blogRepo.findById(blogLanguageDTO.id).orElse(null)
                        val posts = blogPostRepo.findByBlogIdAndState(blogLanguageDTO.id, PostState.PUBLISHED.name)
                            blog.blogPosts = posts.toMutableList()
                            blog
*/
                        //TODO Reier can cause error if no published posts
                       blogRepo.findWithPublishedPostsById(blogLanguageDTO.id).orElse(null)
                    }
                } else {
                    blogRepo.findById(blogLanguageDTO.id).orElse(null)
                }) ?: throw KeyNotFoundException(
            "Blog with id ${blogLanguageDTO.id} not found"
        )
        return blog
    }

    fun readMenu(languageCode: String): List<MenuItem> {
        logger.info("Read menu from blog with language: $languageCode")
        return blogRepo.getMenuItems(languageCode)
    }

    fun save(
        blogId: Long?, blogForm: BlogForm, langCode: String, blogOwner: BlogOwner, timestamp: Instant,
    ) {
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
                mutableListOf<BlogPost>(), blogOwner
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
            throw KeyNotFoundException("Blog not found: ownerId: $blogOwnerId segment: $segment languageCode: $languageCode")
        }
        if (ids.size > 1) {
            throw DuplicateKeyException("Blog duplicate ids: ownerId: $blogOwnerId ids: $ids}")
        }
        return ids.first()
    }

    fun duplicate(segment: String, blogOwnerId: Long, languageCode: String, blogId: Long?): Boolean {
        return blogRepo.findBlogIds(segment, blogOwnerId, languageCode).any { it != blogId }
    }
}
