package com.techreier.edrops.repository

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
    lateinit var languageRepo: LanguageRepository

    @Autowired
    lateinit var blogTextRepo: BlogTextRepository

    @Autowired
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
        languageRepo.save(Norwegian)
        languageRepo.save(English)
        logger.info("Reier før blogOwner")
        blogOwner = ownerRepo.save(blogData.blogOwner)
        blog = blogOwner.blogs.filter {it.segment == ENVIRONMENT}.first()
        blogId =  blog.id!!
        blogEntry = blog.blogEntries.filter {it.segment == ELPOWER }.first()
        blogEntryId = blog.blogEntries.filter {it.segment == ELPOWER }.first().id!!
        noOfBlogEntries = blogOwner.blogs.sumOf { it.blogEntries.size }
        noOfBlogs = blogOwner.blogs.size
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
        blogData.initialize()
    }
}