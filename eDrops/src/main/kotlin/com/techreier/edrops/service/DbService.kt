package com.techreier.edrops.service

import com.techreier.edrops.config.logger
import com.techreier.edrops.domain.Blog
import com.techreier.edrops.domain.BlogOwner
import com.techreier.edrops.domain.LanguageCode
import com.techreier.edrops.domain.Topic
import com.techreier.edrops.dto.MenuItemDTO
import com.techreier.edrops.repository.BlogOwnerRepository
import com.techreier.edrops.repository.BlogRepository
import com.techreier.edrops.repository.LanguageRepository
import com.techreier.edrops.repository.TopicRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DbService(
    private val ownerRepo: BlogOwnerRepository,
    private val blogRepo: BlogRepository,
    private val languageRepo: LanguageRepository,
    private val topicRepo: TopicRepository,
) {
    fun readFirstBlog(blogOwnerId: Long): Blog? {
        logger.info("Read first blog")
        val blogOwner = ownerRepo.findByIdOrNull(blogOwnerId)
        val firstBlog = blogOwner?.blogs?.last()
        logger.info("blogOwner: $blogOwner")
        return firstBlog
    }

    fun readOwner(blogOwnerId: Long): BlogOwner? {
        logger.info("Read blog owner")
        //  return ownerRepo.findByIdOrNull(blogOwnerId)
        return ownerRepo.findById(blogOwnerId).orElse(null)
    }

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
    ): Blog? {
        logger.info("Find blog by languageCode: $languageCode and segment: $segment")
        if (entries) {
            return blogRepo.findWithEntriesByTopicLanguageCodeAndSegment(languageCode, segment)
        } else {
            return blogRepo.findByTopicLanguageCodeAndSegment(languageCode, segment)
        }
    }

    // if language is changed, we try to fetch a blog with the new language and the same segment
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

    fun readBlogs(languageCode: String): MutableSet<Blog> {
        logger.info("Read blogs with language: $languageCode")
        return blogRepo.findByTopicLanguageCode(languageCode)
    }

    fun readMenu(languageCode: String): List<MenuItemDTO> {
        logger.info("Read menu from blog with language: $languageCode")
        return blogRepo.getMenuItems(languageCode)
    }

    fun readLanguages(): MutableList<LanguageCode> = languageRepo.findAll()

    fun readTopics(languageCode: String): MutableList<Topic> {
        val topics = topicRepo.findAllByLanguageCodeOrderByPos(languageCode)
        return topics
    }
}
