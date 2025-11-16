package com.techreier.edrops.dbservice

import com.techreier.edrops.config.logger
import com.techreier.edrops.domain.Blog
import com.techreier.edrops.domain.BlogOwner
import com.techreier.edrops.domain.BlogPost
import com.techreier.edrops.dto.MenuItem
import com.techreier.edrops.forms.BlogForm
import com.techreier.edrops.repository.BlogRepository
import com.techreier.edrops.repository.TopicRepository
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
    fun readBlog(blogId: Long?): Blog? {
        logger.info("Read blog")
        // Does not fetch JPA annotations
        // val blog = blogRepo.findByIdOrNull(blogId)
        return blogId?.let { blogRepo.findWithPostsById(it).orElse(null) }
    }

    // Read current blog based on segment,language code. Assumption: One owner
    fun readBlog(segment: String, oldLangCode: String?, langCode: String, posts: Boolean = false): Blog? {
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
                    blogRepo.findWithPostsById(blogLanguageDTO.id).orElse(null)
                } else {
                    blogRepo.findById(blogLanguageDTO.id).orElse(null)
                }) ?: throw ResponseStatusException(
            HttpStatus.NOT_FOUND,
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
        logger.info("Saving blog with id: ${blogForm.id} segment: ${blogForm.segment} blogId: $blogId")
        val blog: Blog =
            blogId?.let {
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
        logger.info("Deleting blog with id: ${blogForm.id} segment: ${blogForm.segment} blogId: $blogId")
        blogForm.id?.let { id ->
            blogRepo.deleteById(id)
        } ?: logger.error("Blog not deleted, no id")
    }

    fun duplicate(segment: String, blogOwnerId: Long, languageCode: String, blogId: Long?): Boolean {
        return blogRepo.findBlogIds(segment, blogOwnerId, languageCode).any { it != blogId }
    }
}
