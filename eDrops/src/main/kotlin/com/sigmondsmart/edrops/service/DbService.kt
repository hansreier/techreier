package com.sigmondsmart.edrops.service

import com.sigmondsmart.edrops.config.logger
import com.sigmondsmart.edrops.domain.Blog
import com.sigmondsmart.edrops.domain.BlogData
import com.sigmondsmart.edrops.domain.BlogOwner
import com.sigmondsmart.edrops.domain.LanguageCode
import com.sigmondsmart.edrops.repository.BlogOwnerRepository
import com.sigmondsmart.edrops.repository.BlogRepository
import com.sigmondsmart.edrops.repository.LanguageRepository
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DbService(
    private val ownerRepo: BlogOwnerRepository,
    private val blogRepo: BlogRepository,
    private val languageRepo: LanguageRepository
) {

    @PersistenceContext
    lateinit var entityManager: EntityManager

    fun createBlog() {
        logger.info("Create blog")
        ownerRepo.save(BlogData().blogOwner)
        logger.info("saved")
    }

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

    fun readBlog(blogId: Long): Blog? {
        logger.info("Read blog")
        // Does not fetch JPA annotations
        // val blog = blogRepo.findByIdOrNull(blogId)
        return blogRepo.findAllById(blogId).orElse(null)
    }

    fun readBlog(languageCode: String, tag: String): Blog? {
        return blogRepo.findFirstBlogByLanguageAndTag(LanguageCode("", languageCode), tag)
    }

    fun readBlogWithSameLanguage(blogId: Long, langCode: String?): Blog? {
        logger.info("Read blog with  same language")
        val blog = blogRepo.findById(blogId).orElse(null)
        if (langCode != null) {
            logger.debug("The current blog is found with language.code ${blog.language.code}, should be: $langCode")
            if (blog.language.code != langCode) {
                val blogSwitched = blogRepo.findFirstBlogByLanguageAndTag(LanguageCode("", langCode), blog.tag)
                val blogIdNew = blogSwitched?.id ?: blogId
                return blogRepo.findAllById(blogIdNew).orElse(null)
            }
        }
        return blogRepo.findAllById(blogId).orElse(null)
    }

    fun readBlogs(blogOwnerId: Long, languageCode: String): MutableSet<Blog> {
        logger.info("Read blogs with language: $languageCode")
        return blogRepo.findByLanguage(LanguageCode("", languageCode))
    }

    fun readLanguages(): MutableList<LanguageCode> {
        return languageRepo.findAll()
    }
}