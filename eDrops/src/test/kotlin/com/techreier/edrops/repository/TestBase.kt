package com.techreier.edrops.repository

import com.techreier.edrops.config.AppConfig
import com.techreier.edrops.config.logger
import com.techreier.edrops.data.Base
import com.techreier.edrops.data.Initial
import com.techreier.edrops.data.blogs.climatenv.Climatenv
import com.techreier.edrops.data.blogs.climatenv.Green
import com.techreier.edrops.domain.*
import jakarta.persistence.*
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired

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
    lateinit var initial: Initial
    lateinit var blogOwner: BlogOwner
    lateinit var blog: Blog
    lateinit var blogPost: BlogPost
    var blogId: Long = 0
    var blogPostId: Long = 0
    var noOfBlogPosts: Int = 0
    var noOfBlogs: Int = 0

    @BeforeEach
    fun setup() {
        clean()
        val base = Base()
        initial = Initial(appConfig, base)
        languageRepo.saveAll(base.languages)
        topicRepo.saveAll(base.topics)
        blogOwner = ownerRepo.save(initial.blogOwner)
        blog = blogOwner.blogs.first { it.segment == Climatenv.SEGMENT }
        blogId = blog.id!!
        blogPost = blog.blogPosts.first { it.segment == Green.SEGMENT }
        blogPostId = blogPost.id!!
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
}
