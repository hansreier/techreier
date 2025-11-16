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
        val clone = Initial(appConfig)
        languageRepo.saveAll(initial.base.languages)
        topicRepo.saveAll(initial.base.topics)
        blogOwnerRepo.save(initial.blogOwner)
        val owner = clone.blogOwner.username
        initService.saveInitialData(clone)
        val blogOwner = blogOwnerRepo.findBlogOwnerByUsername(owner)
        assertNotNull(blogOwner)
        assertEquals("Sigmond", blogOwner.lastName)
        assertEquals("reier.sigmond@gmail.com", blogOwner.eMail)
    }

    // TODO Tests that confirms what the latest record inserted is used  need to be added

    @Test
    fun happyMergeDBTest() {

        // initialize database
        val initial = Initial(appConfig)
        languageRepo.saveAll(initial.base.languages)
        topicRepo.saveAll(initial.base.topics)
        val blogOwner = blogOwnerRepo.save(initial.blogOwner)

        val blogIdFirst = blogOwner.blogs.first().id ?: fail("first blog id not found")
        val blogIdLast = blogOwner.blogs.last().id ?: fail("last blog id not found")
        val blogChangedLast = blogOwner.blogs.last().changed
        assertNotEquals(blogIdFirst, blogIdLast,"only one blog is wrong")

        blogOwner.blogs
        val secondBlog = blogOwner.blogs.iterator().next()
        val postIdFirst = secondBlog.blogPosts.first().id ?: fail("first blogpost id not found")
        val postIdLast = secondBlog.blogPosts.last().id ?: fail("last blog post id not found")
        val postChangedLast = secondBlog.blogPosts.last().changed
        assertNotEquals(postIdFirst, postIdLast,"only one blog post is wrong")

        // change a clone, first and last blog,  and first and last blog post in the first blog
        val clone = Initial(appConfig)
        val owner = clone.blogOwner
        val firstBlog = owner.blogs.first()
        firstBlog.changed = firstBlog.changed.plus(Duration.ofDays(1))
        firstBlog.pos = Int.MIN_VALUE
        val lastBlog = owner.blogs.last()
        lastBlog.changed = lastBlog.changed.minus(Duration.ofDays(1))
        lastBlog.pos = Int.MAX_VALUE

        val firstPost = secondBlog.blogPosts.first()
        firstPost.changed = firstPost.changed.plus(Duration.ofDays(1))
        firstPost.title += "#ChangedFirst"
        val lastPost = firstBlog.blogPosts.last()
        lastPost.changed = lastPost.changed.minus(Duration.ofDays(1))
        lastPost.title += "#ChangedLast"

        initService.saveInitialData(clone)

        val blogFirst = blogRepo.findById(blogIdFirst).orElse(null)?: fail("blog id not found")
        assertThat(blogFirst.changed).isCloseTo(firstBlog.changed, within(5, ChronoUnit.SECONDS))
        assertEquals(Int.MIN_VALUE, blogFirst.pos)

        val blogLast = blogRepo.findById(blogIdLast).orElse(null)?: fail("blog id not found")
        assertThat(blogLast.changed).isCloseTo(blogChangedLast, within(5, ChronoUnit.SECONDS))
        assertNotEquals(Int.MAX_VALUE, blogLast.pos)

        val postFirst = blogPostRepo.findById(postIdFirst).orElse(null)?: fail("blog post id not found")
        assertThat(postFirst.changed).isCloseTo(firstPost.changed, within(5, ChronoUnit.SECONDS))
        assertEquals(firstPost.title, postFirst.title)

        val postLast = blogPostRepo.findById(postIdLast).orElse(null)?: fail("blog post id not found")
        assertThat(postLast.changed).isCloseTo(postChangedLast, within(5, ChronoUnit.SECONDS))
        assertNotEquals(lastPost.title, postLast.title)

    }

    @Test
    fun uniqueTopicLanguageCodeTest() {
        val initial = Initial(appConfig)
        val clone = Initial(appConfig)
        languageRepo.saveAll(initial.base.languages)

        val topic = Topic(TOPIC_DEFAULT, initial.base.norwegian, 0)

        topicRepo.save(topic)
        assertThrows<DataIntegrityViolationException> {
            initService.saveInitialData(clone)
        }
    }

    @Test
    fun duplicateBlogSegmentTest() {
        val initial = Initial(appConfig)
        val clone = Initial(appConfig)
        languageRepo.saveAll(initial.base.languages)
        topicRepo.saveAll(initial.base.topics)
        blogOwnerRepo.save(initial.blogOwner)
        val first = initial.blogOwner.blogs.first()
        initial.blogOwner.blogs.last().copyAttributes(first)
        val e = assertThrows<DuplicateKeyException> {
            initService.saveInitialData(clone)
        }
        assertThat(e.message).contains("Duplicate blog")
    }

    @Test
    fun duplicateBlogPostSegmentTest() {
        val initial = Initial(appConfig)
        val clone = Initial(appConfig)
        languageRepo.saveAll(initial.base.languages)
        topicRepo.saveAll(initial.base.topics)
        blogOwnerRepo.save(initial.blogOwner)
        val first = initial.blogOwner.blogs.first().blogPosts.first()
        initial.blogOwner.blogs.first().blogPosts.last().copyAttributes(first)
        val e = assertThrows<DuplicateKeyException> {
            initService.saveInitialData(clone)
        }
        assertThat(e.message).contains("Duplicate blogpost")
    }
}