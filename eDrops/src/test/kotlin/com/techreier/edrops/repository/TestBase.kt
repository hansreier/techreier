package com.techreier.edrops.repository

import com.techreier.edrops.config.AppConfig
import com.techreier.edrops.config.logger
import com.techreier.edrops.domain.*
import jakarta.persistence.*
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired

abstract class TestBase {

    @PersistenceContext
    lateinit var entityManager: EntityManager

    @Autowired
    lateinit var entryRepo: BlogEntryRepository

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
    lateinit var blog: Blog
    lateinit var blogEntry: BlogEntry
    var blogId: Long = 0
    var blogEntryId: Long = 0
    var noOfBlogEntries: Int = 0
    var noOfBlogs: Int = 0

    @BeforeEach
    fun setup() {
        clean()
        blogData = BlogData(appConfig)
        languageRepo.save(Norwegian)
        languageRepo.save(English)
        topicRepo.save(blogData.defaultN)
        topicRepo.save(blogData.defaultE)
        topicRepo.save(blogData.energyE)
        topicRepo.save(blogData.energyN)
        blogOwner = ownerRepo.save(blogData.blogOwner)
        blog = blogOwner.blogs.first { it.segment == ENVIRONMENT }
        blogId =  blog.id!!
        blogEntry = blog.blogEntries.first { it.segment == ELPOWER }
        blogEntryId = blog.blogEntries.first { it.segment == ELPOWER }.id!!
        noOfBlogEntries = blogOwner.blogs.sumOf { it.blogEntries.size }
        noOfBlogs = blogOwner.blogs.size
        entityManager.flush()
    }

    // Does not clean sequences in id's, but really does not matter
    private fun clean() {
        logger.info("CleanUp start")
        entityManager.clear()
        entityManager.createQuery("DELETE FROM BlogText").executeUpdate()
        entityManager.createQuery("DELETE FROM BlogEntry").executeUpdate()
        entityManager.createQuery("DELETE FROM Blog").executeUpdate()
        entityManager.createQuery("DELETE FROM BlogOwner").executeUpdate()
        entityManager.createQuery("DELETE FROM LanguageCode").executeUpdate()
        entityManager.flush()
        entityManager.clear()
    }
}