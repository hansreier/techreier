package com.sigmondsmart.edrops.service

import com.sigmondsmart.edrops.config.logger
import com.sigmondsmart.edrops.domain.BlogData
import com.sigmondsmart.edrops.repository.BlogOwnerRepository
import org.springframework.stereotype.Service

@Service
class DbService(private val ownerRepo: BlogOwnerRepository) {

    fun createBlog() {
        logger.info("Create blog")
        ownerRepo.save(BlogData().blogOwner)
        logger.info("saved")
    }

    fun readBlog() {
        logger.info("Read blog")
    val blog = ownerRepo.findAll()
        logger.info("$blog")
        blog.forEach {
            it.blogEntries?.forEach {
                logger.info("$it.text")
            }
        }
    }


}