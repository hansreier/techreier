package com.techreier.edrops.dbservice

import com.techreier.edrops.config.logger
import com.techreier.edrops.domain.Blog
import com.techreier.edrops.dto.MenuItemDTO
import com.techreier.edrops.repository.BlogRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class BlogService(
    private val blogRepo: BlogRepository,
) {
    fun readBlog(blogId: Long?): Blog? {
        logger.info("Read blog")
        // Does not fetch JPA annotations
        // val blog = blogRepo.findByIdOrNull(blogId)
        return blogId?.let { blogRepo.findWithEntriesById(it).orElse(null) }
    }

    // TODO entries is always true calling this. Simplify later?
    fun findBlog(
        languageCode: String,
        segment: String,
        entries: Boolean = false,
    ): Blog? {
        logger.info("Find blog by languageCode: $languageCode and segment: $segment")
        if (entries) {
            return blogRepo.findWithEntriesByTopicLanguageCodeAndSegment(languageCode, segment)
        } else {
            return blogRepo.findByTopicLanguageCodeAndSegment(languageCode, segment)
        }
    }

    // if language is changed, we try to fetch a blog with the new language and the same segment
    // TODO entries is always true calling this. Simplify later?
    fun readBlogWithSameLanguage(
        blogId: Long,
        langCode: String?,
        entries: Boolean = false,
    ): Blog? {
        logger.info("Read blog with same language 2: $langCode as blog with id $blogId")
        var blogIdNew = blogId
        if (langCode != null) {
            val blog = blogRepo.findById(blogId).orElse(null)
            if (blog != null) {
                logger.debug("The current blog is found with language.code ${blog.topic.language.code}, should be: $langCode")
                if (blog.topic.language.code != langCode) {
                    val blogSwitched = blogRepo.findByTopicLanguageCodeAndSegment(langCode, blog.segment)
                    if (!entries) return blogSwitched
                    blogIdNew = blogSwitched?.id ?: blogId
                }
                if (!entries) return blog
            }
        }
        return blogRepo.findWithEntriesById(blogIdNew).orElse(null)
    }

    fun readMenu(languageCode: String): List<MenuItemDTO> {
        logger.info("Read menu from blog with language: $languageCode")
        return blogRepo.getMenuItems(languageCode)
    }
}
