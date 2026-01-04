package com.techreier.edrops.dbservice

import com.techreier.edrops.data.Initial
import com.techreier.edrops.data.TOPIC_DEFAULT
import com.techreier.edrops.domain.Topic
import com.techreier.edrops.repository.TestBase
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
class InitServiceTest : TestBase() {

    @Autowired
    private lateinit var initService: InitService

    @Test
    fun emptyDBTest() {
        val initial = Initial(appConfig)
        val owner = initial.blogOwner.username
        initService.saveInitialData(initial)
        val blogOwner = ownerRepo.findBlogOwnerByUsername(owner)
        assertNotNull(blogOwner)
        assertEquals("Sigmond", blogOwner.lastName)
        assertEquals("reier.sigmond@gmail.com", blogOwner.eMail)
    }

    @Test
    fun mergeDBNoChangesTest() {
        val clone = Initial(appConfig)
        val owner = clone.blogOwner.username
        initService.saveInitialData(clone)
        val blogOwner = ownerRepo.findBlogOwnerByUsername(owner)
        assertNotNull(blogOwner)
        assertEquals("Sigmond", blogOwner.lastName)
        assertEquals("reier.sigmond@gmail.com", blogOwner.eMail)
    }

    @Test
    fun mergeDBWithChangesTest() {

        assertThat(blogOwner.blogs.size).isGreaterThan(2)
        val blogIdFirst = blogOwner.blogs.first().id ?: fail("first blog id not found")
        val blogIdLast = blogOwner.blogs.last().id ?: fail("last blog id not found")
        val blogTimestampLast = blogOwner.blogs.last().changed
        val blogPosLast = blogOwner.blogs.last().pos
        assertNotEquals(blogIdFirst, blogIdLast, "only one blog is wrong")

        val secondBlog = blogOwner.blogs.iterator().next()
        assertThat(secondBlog.blogPosts.size).isGreaterThan(1)
        val postIdFirst = secondBlog.blogPosts.first().id ?: fail("first blogpost id not found")
        val postIdLast = secondBlog.blogPosts.last().id ?: fail("last blog post id not found")
        val postTimestampLast = secondBlog.blogPosts.last().changed
        val postTitleLast = secondBlog.blogPosts.last().title
        assertNotEquals(postIdFirst, postIdLast, "only one blog post is wrong")

        // Change a clone: First and last blog,  and first and last blog post in the second blog
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
        val lastPost = secondBlog.blogPosts.last()
        lastPost.changed = lastPost.changed.minus(Duration.ofDays(1))
        lastPost.title += "#ChangedLast"

        val topics = clone.base.topics
        topics.addLast(Topic("Dummy", initial.base.norwegian))
        topics.addLast(Topic("Dummy", initial.base.english))
        topics.first().text = "Dummy"
        //Save initial transient data just like when starting the server
        initService.saveInitialData(clone)

        //First blog changed because data was changed after current timestamp in DB
        val blogFirst = blogRepo.findById(blogIdFirst).orElse(null) ?: fail("blog id not found")
        assertThat(blogFirst.changed).isCloseTo(firstBlog.changed, within(5, ChronoUnit.SECONDS))
        assertEquals(Int.MIN_VALUE, blogFirst.pos)

        //Last blog not changed because data was changed before  current timestamp in DB
        val blogLast = blogRepo.findById(blogIdLast).orElse(null) ?: fail("blog id not found")
        assertThat(blogLast.changed).isCloseTo(blogTimestampLast, within(5, ChronoUnit.SECONDS))
        assertEquals(blogPosLast, blogLast.pos)

        //First post changed because data was changed after current timestamp in DB
        val postFirst = postRepo.findById(postIdFirst).orElse(null) ?: fail("blog post id not found")
        assertThat(postFirst.changed).isCloseTo(firstPost.changed, within(5, ChronoUnit.SECONDS))
        assertEquals(firstPost.title, postFirst.title)

        //First post not changed because data was changed before current timestamp in DB
        val postLast = postRepo.findById(postIdLast).orElse(null) ?: fail("blog post id not found")
        assertThat(postLast.changed).isCloseTo(postTimestampLast, within(5, ChronoUnit.SECONDS))
        assertEquals(postTitleLast, postLast.title)

        //Check changes in topics list
        val dummyTopicNo = topicRepo.findByTopicKeyAndLanguageCode("Dummy", initial.base.norwegian.code)
        val dummyTopicEn = topicRepo.findByTopicKeyAndLanguageCode("Dummy", initial.base.english.code)
        val firstTopic: Topic = topicRepo.findById(1).orElse(null)
        assertNotNull(firstTopic)
        assertEquals("Dummy",firstTopic.text)
        assertNotNull(dummyTopicNo)
        assertNotNull(dummyTopicEn)

    }

    @Test
    fun uniqueTopicLanguageCodeTest() {
        Initial(appConfig)
        val topic = Topic(TOPIC_DEFAULT, initial.base.norwegian, 0)
        assertThrows<DataIntegrityViolationException> {
            topicRepo.save(topic)
        }
    }

    @Test
    fun duplicateBlogSegmentTest() {
        val clone = Initial(appConfig)
        val first = initial.blogOwner.blogs.first()
        initial.blogOwner.blogs.last().copyAttributes(first)
        val e = assertThrows<DuplicateKeyException> {
            initService.saveInitialData(clone)
        }
        assertThat(e.message).contains("Duplicate blog")
    }

    @Test
    fun duplicateBlogPostSegmentTest() {
        val clone = Initial(appConfig)
        val first = initial.blogOwner.blogs.first().blogPosts.first()
        initial.blogOwner.blogs.first().blogPosts.last().copyAttributes(first)
        val e = assertThrows<DuplicateKeyException> {
            initService.saveInitialData(clone)
        }
        assertThat(e.message).contains("Duplicate blogpost")
    }
}