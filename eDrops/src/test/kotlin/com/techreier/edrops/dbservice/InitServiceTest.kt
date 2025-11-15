package com.techreier.edrops.dbservice

import com.techreier.edrops.config.AppConfig
import com.techreier.edrops.config.logger
import com.techreier.edrops.data.Initial
import com.techreier.edrops.data.TOPIC_DEFAULT
import com.techreier.edrops.domain.Topic
import com.techreier.edrops.repository.BlogOwnerRepository
import com.techreier.edrops.repository.BlogPostRepository
import com.techreier.edrops.repository.BlogRepository
import com.techreier.edrops.repository.LanguageRepository
import com.techreier.edrops.repository.TopicRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.dao.DuplicateKeyException
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
class InitServiceTest {

    @Autowired
    private lateinit var initService: InitService

    @Autowired
    private lateinit var blogOwnerRepo: BlogOwnerRepository

    @Autowired
    private lateinit var blogRepo: BlogRepository

    @Autowired
    private lateinit var blogPostRepo: BlogPostRepository

    @Autowired
    private lateinit var languageRepo: LanguageRepository

    @Autowired
    private lateinit var topicRepo: TopicRepository

    @Autowired
    private lateinit var appConfig: AppConfig

    @Test
    fun checkSpringConttext() {
        logger.info("Spring Context OK")
    }

    @Test
    fun happyEmptyDBTest() {
        val initial = Initial(appConfig)
        val owner = initial.blogOwner.username
        initService.saveInitialData(initial)
        val blogOwner = blogOwnerRepo.findBlogOwnerByUsername(owner)
        assertNotNull(blogOwner)
        assertEquals("Sigmond", blogOwner.lastName)
        assertEquals("reier.sigmond@gmail.com", blogOwner.eMail)
    }

    @Test
    fun happyInitializedDBTest() {
        val initial = Initial(appConfig)
        languageRepo.saveAll(initial.base.languages)
        topicRepo.saveAll(initial.base.topics)
        blogOwnerRepo.save(initial.blogOwner)
        val owner = initial.blogOwner.username
        initService.saveInitialData(initial)
        val blogOwner = blogOwnerRepo.findBlogOwnerByUsername(owner)
        assertNotNull(blogOwner)
        assertEquals("Sigmond", blogOwner.lastName)
        assertEquals("reier.sigmond@gmail.com", blogOwner.eMail)
    }

    // TODO Tests that confirms what the latest record inserted is used  need to be added

    @Test
    fun uniqueTopicLanguageCodeTest() {
        val initial = Initial(appConfig)
        languageRepo.saveAll(initial.base.languages)

        val topic = Topic(TOPIC_DEFAULT, initial.base.norwegian, 0)

        topicRepo.save(topic)
        assertThrows<DataIntegrityViolationException> {
            initService.saveInitialData(initial)
        }
    }

    @Test
    fun duplicateBlogSegmentTest() {
        val initial = Initial(appConfig)
        languageRepo.saveAll(initial.base.languages)
        topicRepo.saveAll(initial.base.topics)
        blogOwnerRepo.save(initial.blogOwner)
        val first = initial.blogOwner.blogs.first()
        val duplicate = initial.blogOwner.blogs.last().copyAttributes(first)
        blogRepo.save(duplicate)
        val e = assertThrows<DuplicateKeyException> {
            initService.saveInitialData(initial)
        }
        assertThat(e.message).contains("Duplicate blog")
    }

    @Test
    fun duplicateBlogPostSegmentTest() {
        val initial = Initial(appConfig)
        languageRepo.saveAll(initial.base.languages)
        topicRepo.saveAll(initial.base.topics)
        blogOwnerRepo.save(initial.blogOwner)
        val first = initial.blogOwner.blogs.first().blogPosts.first()
        val duplicate = initial.blogOwner.blogs.first().blogPosts.last().copyAttributes(first)
        blogPostRepo.save(duplicate)
        val e = assertThrows<DuplicateKeyException> {
            initService.saveInitialData(initial)
        }
        assertThat(e.message).contains("Duplicate blogpost")
    }
}