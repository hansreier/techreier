package com.techreier.edrops.dbservice

import com.techreier.edrops.config.logger
import com.techreier.edrops.domain.Blog
import com.techreier.edrops.dto.BlogDTO
import com.techreier.edrops.dto.MenuItemDTO
import com.techreier.edrops.dto.toDTO
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

    fun findBlog(
        languageCode: String,
        segment: String,
        entries: Boolean = false,
    ): BlogDTO? {
        logger.info("Find blog by languageCode: $languageCode and segment: $segment")
        if (entries) {
            return blogRepo.findWithEntriesByTopicLanguageCodeAndSegment(languageCode, segment)?.toDTO(languageCode, true)
        } else {
            return blogRepo.findByTopicLanguageCodeAndSegment(languageCode, segment)?.toDTO(languageCode, false)
        }
    }

    // if language is changed, we try to fetch a blog with the new language and the same segment
    fun readBlog(
        blogId: Long,
        langCode: String?,
        entries: Boolean = false,
    ): BlogDTO? {
        logger.info("Read blog with same language: $langCode as blog with id $blogId")
        var blogIdNew = blogId

        val blog = blogRepo.findById(blogId).orElse(null) ?: return null

        logger.debug("The current blog is found with language.code ${blog.topic.language.code}, should be: $langCode")
        if (langCode != null) {
            if (blog.topic.language.code != langCode) { // language is changed.
                val blogSwitched = blogRepo.findByTopicLanguageCodeAndSegment(langCode, blog.segment)
                if (blogSwitched != null) {
                    if (!entries) return blogSwitched.toDTO(langCode, false)
                    blogIdNew = blogSwitched.id ?: blogId
                }
            }
        }
        return if (entries)
            blogRepo.findWithEntriesById(blogIdNew).orElse(null)?.toDTO(langCode)
        else
            blog.toDTO(langCode, false)
    }

    fun readMenu(languageCode: String): List<MenuItemDTO> {
        logger.info("Read menu from blog with language: $languageCode")
        return blogRepo.getMenuItems(languageCode)
    }

}
