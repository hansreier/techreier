package com.sigmondsmart.edrops.service

import com.sigmondsmart.edrops.config.logger
import com.sigmondsmart.edrops.domain.Blog
import com.sigmondsmart.edrops.domain.BlogData
import com.sigmondsmart.edrops.domain.BlogOwner
import com.sigmondsmart.edrops.domain.LanguageCode
import com.sigmondsmart.edrops.repository.BlogOwnerRepository
import com.sigmondsmart.edrops.repository.BlogRepository
import com.sigmondsmart.edrops.repository.LanguageRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DbService(private val ownerRepo: BlogOwnerRepository,
                private val blogRepo: BlogRepository,
                private val languageRepo: LanguageRepository
) {

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
        val blog = blogRepo.findById(blogId).orElse(null)
        return blog
    }

    fun readBlogs(blogOwnerId: Long, languageCode: String): MutableSet<Blog>? {
        logger.info("Read blogs with language: $languageCode")
        val blogs = blogRepo.findByLanguage(LanguageCode("", languageCode))
        return blogs
    }

    fun readLanguages(): MutableList<LanguageCode> {
        return languageRepo.findAll()
    }
}