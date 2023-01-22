package com.sigmondsmart.edrops.service

import com.sigmondsmart.edrops.config.logger
import com.sigmondsmart.edrops.domain.Blog
import com.sigmondsmart.edrops.domain.BlogData
import com.sigmondsmart.edrops.domain.BlogOwner
import com.sigmondsmart.edrops.repository.BlogOwnerRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class DbService(private val ownerRepo: BlogOwnerRepository) {

    fun createBlog() {
        logger.info("Create blog")
        ownerRepo.save(BlogData().blogOwner)
        logger.info("saved")
    }

    fun readFirstBlog(blogOwnerId: Long): Blog? {
        logger.info("Read blog")
    val blogOwner = ownerRepo.findByIdOrNull(blogOwnerId)
    val firstBlog =  blogOwner?.blogs?.last()
        logger.info("blogOwner: $blogOwner")
        return firstBlog
    }

    fun readOwner(blogOwnerId: Long): BlogOwner? {
        logger.info("Read blog")
        return ownerRepo.findByIdOrNull(blogOwnerId)
    }
}