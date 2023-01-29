package com.sigmondsmart.edrops.service

import com.sigmondsmart.edrops.config.logger
import com.sigmondsmart.edrops.domain.Blog
import com.sigmondsmart.edrops.domain.BlogData
import com.sigmondsmart.edrops.domain.BlogOwner
import com.sigmondsmart.edrops.repository.BlogOwnerRepository
import com.sigmondsmart.edrops.repository.BlogRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class DbService(private val ownerRepo: BlogOwnerRepository, private val blogRepo: BlogRepository) {

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
        logger.info("Read blog owner")
        return ownerRepo.findByIdOrNull(blogOwnerId)
    }

    fun readBlog(blogId: Long) : Blog? {
        logger.info("Read blog")
        return blogRepo.findByIdOrNull(blogId)
    }
}