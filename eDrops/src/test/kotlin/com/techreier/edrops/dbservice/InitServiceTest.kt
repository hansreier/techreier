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
import org.assertj.core.api.Assertions.within
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.dao.DuplicateKeyException
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.temporal.ChronoUnit
import kotlin.test.DefaultAsserter.fail

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
    fun happyOverwriteDBTest() {
        // Save initial data
        val initial = Initial(appConfig)
        languageRepo.saveAll(initial.base.languages)
        topicRepo.saveAll(initial.base.topics)
        val blogOwner = blogOwnerRepo.save(initial.blogOwner)

        val blogIdFirst = blogOwner.blogs.first().id ?: fail("first id not found")
        val blogIdLast = blogOwner.blogs.last().id ?: fail("last id not found")
        val blogChangedLast = blogOwner.blogs.last().changed
        assertNotEquals(blogIdFirst, blogIdLast,"only one blog is wrong")

        //Change first and last blog and perform initial save of data
        val owner = initial.blogOwner
        val first = owner.blogs.first()
        first.changed = first.changed.plus(Duration.ofDays(1))
        first.pos = Int.MIN_VALUE
        val last = owner.blogs.last()
        last.changed = last.changed.minus(Duration.ofDays(1))
        last.pos = Int.MAX_VALUE
        initService.saveInitialData(initial)

        val blogFirst = blogRepo.findById(blogIdFirst).orElse(null)?: fail("id not found")
        assertNotNull(blogFirst)
        assertThat(blogFirst.changed).isCloseTo(first.changed, within(5, ChronoUnit.SECONDS))
        assertEquals(Int.MIN_VALUE, blogFirst.pos)

        val blogLast = blogRepo.findById(blogIdLast).orElse(null)?: fail("id not found")
        assertThat(blogLast.changed).isCloseTo(blogChangedLast, within(5, ChronoUnit.SECONDS))
        assertNotEquals(Int.MAX_VALUE, blogLast.pos)

    }

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