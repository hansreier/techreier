package com.sigmondsmart.edrops.repository

import com.sigmondsmart.edrops.config.log
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.annotation.Transactional

@ExtendWith(SpringExtension::class)
@DataJpaTest
class TestBlog {

    @Autowired
    lateinit var entryRepo: BlogEntryRepository

    @Autowired
    lateinit var ownerRepo: BlogOwnerRepository

    @Test
    @DirtiesContext
    fun `owner with blog entry check`() {
        with(BlogData()) {
            log.info("reiers starting transactional test")
            ownerRepo.save(blogOwner)
            entryRepo.save(blogEntry)
            log.info("completed")
        }
    }
}