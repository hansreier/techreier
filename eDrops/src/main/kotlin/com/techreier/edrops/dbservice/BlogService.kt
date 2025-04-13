package com.techreier.edrops.dbservice

import com.techreier.edrops.config.logger
import com.techreier.edrops.domain.Blog
import com.techreier.edrops.domain.BlogOwner
import com.techreier.edrops.domain.BlogPost
import com.techreier.edrops.dto.BlogDTO
import com.techreier.edrops.dto.MenuItem
import com.techreier.edrops.dto.toDTO
import com.techreier.edrops.forms.BlogForm
import com.techreier.edrops.repository.BlogRepository
import com.techreier.edrops.repository.TopicRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.time.ZonedDateTime

@Service
@Transactional
class BlogService(
    private val blogRepo: BlogRepository,
    private val topicRepo: TopicRepository,
) {
    // TODO not used. Consider removing
    fun readBlog(blogId: Long?): Blog? {
        logger.info("Read blog")
        // Does not fetch JPA annotations
        // val blog = blogRepo.findByIdOrNull(blogId)
        return blogId?.let { blogRepo.findWithPostsById(it).orElse(null) }
    }

    // Read current blog based on segment,language code. Assumption: One owner
    fun readBlog(
        segment: String, oldLangCode: String?, langCode: String, posts: Boolean = false,
    ): BlogDTO? {
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

        //TODO fix this warning
        val blog: Blog? = (
                if (posts) {
                    blogRepo.findWithPostsById(blogLanguageDTO.id).orElse(null)
                } else {
                    blogRepo.findById(blogLanguageDTO.id).orElse(null)
                }) ?: throw ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "Blog with id ${blogLanguageDTO.id} not found"
        )

        return blog!!.toDTO(langCode, posts)
    }

    fun readMenu(languageCode: String): List<MenuItem> {
        logger.info("Read menu from blog with language: $languageCode")
        return blogRepo.getMenuItems(languageCode)
    }

    fun save(
        blogId: Long?, blogForm: BlogForm, langCode: String, blogOwner: BlogOwner,
    ) {
        logger.info("Saving blog with id: ${blogForm.id} segment: ${blogForm.segment} blogId: $blogId")
        val blog: Blog =
            blogId?.let {
                val foundBlog: Blog? = blogRepo.findById(it).orElse(null)
                    ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Blog with id $it not found")
                if ((foundBlog!!.topic.topicKey != blogForm.topicKey) || foundBlog.topic.language.code != langCode) {
                    topicRepo.findFirstByTopicKeyAndLanguageCode(blogForm.topicKey, langCode).orElse(null)
                        ?.let { topic -> foundBlog.topic = topic }
                        ?: logger.warn("Topic with key: ${blogForm.topicKey} and languageCode: $langCode not found")
                }
                foundBlog.changed = ZonedDateTime.now()
                foundBlog.segment = blogForm.segment
                foundBlog.pos = blogForm.position.toIntOrNull() ?: 0
                foundBlog.subject = blogForm.subject
                foundBlog.about = blogForm.about
                foundBlog
            } ?: Blog(
                ZonedDateTime.now(),
                blogForm.segment,
                topicRepo.findFirstByTopicKeyAndLanguageCode(blogForm.topicKey, langCode).orElse(null)
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

    fun exists(segment: String, blogOwnerId: Long, languageCode: String) : Boolean {
        return blogRepo.findBlogIds(segment, blogOwnerId, languageCode).isNotEmpty()
    }
}
