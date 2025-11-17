package com.techreier.edrops.dbservice

import com.techreier.edrops.config.AppConfig
import com.techreier.edrops.config.logger
import com.techreier.edrops.data.Initial
import com.techreier.edrops.repository.BlogOwnerRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
class InitServiceSimpleTest {

    @Autowired
    lateinit var appConfig: AppConfig

    @Autowired
    private lateinit var initService: InitService

    @Autowired
    lateinit var ownerRepo: BlogOwnerRepository

    @Test
    fun checkSpringConttext() {
        logger.info("Spring Context OK")
    }

    @Test
    @DirtiesContext
    fun emptyDBTest() {
        val initial = Initial(appConfig)
        val owner = initial.blogOwner.username
        initService.saveInitialData(initial)
        val blogOwner = ownerRepo.findBlogOwnerByUsername(owner)
        assertNotNull(blogOwner)
        assertEquals("Sigmond", blogOwner.lastName)
        assertEquals("reier.sigmond@gmail.com", blogOwner.eMail)
        assertThat(blogOwner.blogs.first().blogPosts.count()).isGreaterThan(0)
    }

}