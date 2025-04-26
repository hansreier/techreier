package com.techreier.edrops.repository

import com.techreier.edrops.config.AppConfig
import com.techreier.edrops.config.logger
import com.techreier.edrops.domain.*
import jakarta.persistence.*
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

abstract class TestBase {
    @PersistenceContext
    lateinit var entityManager: EntityManager

    @Autowired
    lateinit var postRepo: BlogPostRepository

    @Autowired
    lateinit var blogRepo: BlogRepository

    @Autowired
    lateinit var ownerRepo: BlogOwnerRepository

    @Autowired
    lateinit var topicRepo: TopicRepository

    @Autowired
    lateinit var languageRepo: LanguageRepository

    @Autowired
    lateinit var blogTextRepo: BlogTextRepository

    @Autowired
    lateinit var appConfig: AppConfig
    lateinit var blogData: BlogData
    lateinit var blogOwner: BlogOwner
    lateinit var common: Common
    lateinit var blog: Blog
    lateinit var blogPost: BlogPost
    var blogId: Long = 0
    var blogPostId: Long = 0
    var noOfBlogPosts: Int = 0
    var noOfBlogs: Int = 0

    @BeforeEach
    fun setup() {
        clean()
        common = Common()
        blogData = BlogData(appConfig, common)
        languageRepo.saveAll(common.languages)
        topicRepo.saveAll(common.topics)
        blogOwner = ownerRepo.save(blogData.blogOwner)
        blog = blogOwner.blogs.first { it.segment == BSEG_ENVIRONMENT }
        blogId = blog.id!!
        blogPost = blog.blogPosts.first { it.segment == ESEG_SUSTAINABILITY }
        blogPostId = blog.blogPosts.first { it.segment == ESEG_SUSTAINABILITY }.id!!
        noOfBlogPosts = blogOwner.blogs.sumOf { it.blogPosts.size }
        noOfBlogs = blogOwner.blogs.size
        entityManager.flush()
    }

    // Does not clean sequences in id's, but really does not matter
    private fun clean() {
        logger.info("CleanUp start")
        entityManager.clear()
        entityManager.createQuery("DELETE FROM BlogText").executeUpdate()
        entityManager.createQuery("DELETE FROM BlogPost").executeUpdate()
        entityManager.createQuery("DELETE FROM Blog").executeUpdate()
        entityManager.createQuery("DELETE FROM BlogOwner").executeUpdate()
        entityManager.createQuery("DELETE FROM Topic").executeUpdate()
        entityManager.createQuery("DELETE FROM LanguageCode").executeUpdate()
        entityManager.flush()
        entityManager.clear()
        logger.info("cleanup end")
    }

    protected fun timeStamp(): ZonedDateTime = ZonedDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS)
}
