package com.techreier.edrops.repository

import com.techreier.edrops.config.logger
import com.techreier.edrops.domain.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.annotation.Transactional


@ExtendWith(SpringExtension::class)
@SpringBootTest
@Transactional
class BlogTest : TestBase() {

    @Test
    //Do not use this Kotlin extension, does not read JPA annotations, generates more SQL statements
    fun `read with findByIdOrNull`() {
            logger.info("starting read with findByIdOrNull")
            entityManager.clear()
            val blog = blogRepo.findByIdOrNull(blogId)
            assertNotNull(blog)
            assertEquals(this.blog.id, blog?.id)
            logger.info("blog: $blog")
    }

    @Test
    fun `read with findById`() {
        logger.info("starting read with findById")
        entityManager.clear()
        val blog1 = blogRepo.findById(blogId).orElse(null)
        assertNotNull(blog1)
        assertEquals(this.blog.id, blog1.id)
        logger.info("blog: $blog1")
    }

    @Test
    fun `test findAll`() {
        val instances = blogRepo.findAll()
        assertThat(instances.size).isEqualTo(noOfBlogs)
    }

    @Test
    fun `cascade delete test`() {
        logger.info("starting cascade delete test")
        val blog1 = blogOwner.blogs?.first()
        val blogEntry1 = blog1?.blogEntries?.first()!!
        assertNotNull(blogEntry1)
        logger.info("blogEntry: $blogEntry1")
        val blogEntrySaved = entryRepo.findById(blogEntry1.id!!).orElse(null)
        assertThat(blogEntrySaved.id).isEqualTo(blogEntry1.id)
        blogRepo.delete(blog1)
        logger.info("Deleted")
        val blogEntryDeleted = entryRepo.findById(blogEntry1.id).orElse(null)
        assertThat(blogEntryDeleted).isNull()
        logger.info("completed")
    }

    @Test
    fun `read blog with JPQL`() {
        logger.info("starting transactional test")
        val query =
            entityManager.createQuery("SELECT DISTINCT b FROM Blog b INNER JOIN FETCH b.blogEntries WHERE b.subject = ?1 ")
        val blog = query.setParameter(1, SUBJECT1).singleResult as Blog
        logger.info("blog: $blog")
        assertThat(blog.language.language).isEqualTo(NORWEGIAN)
        assertThat(blog.segment).isEqualTo(ENVIRONMENT)
    }

    @Test
    fun `read blog by language and segment`() {
        logger.info("starting read blog by language and segment")
        val blog1 = blogRepo.findFirstBlogByLanguageAndSegment(Norwegian, ENVIRONMENT)
        assertThat(blog1).isNotNull
        assertThat(blog1?.language?.code).isEqualTo(NB)
        assertThat(blog1?.language?.language).isEqualTo(NORWEGIAN)
        assertThat(blog1?.segment).isEqualTo(ENVIRONMENT)
        logger.info("blog: $blog1 ${blog1?.blogEntries?.size}")
    }

    @Test
    //Using JPQL more efficient, only one SQL statement
    //https://www.baeldung.com/spring-data-jpa-named-entity-graphs
    fun `read all with findAll`() {
        logger.info("starting read all test")
        val blogs = blogRepo.findAll()
        logger.info("blogs: $blogs noOfBlogs: ${blogs.size}")
        assertThat(blogs.size).isEqualTo(noOfBlogs)
        blogs.forEach {
            val entries = it.blogEntries
            logger.info("my entries: $entries")
        }
    }

    @Test
    //https://www.baeldung.com/jpa-entity-graph
    fun `read with manual entityGraph`() {
        logger.info("starting read all test")
        val languageCode = LanguageCode(NORWEGIAN, NB)
        val blog1 = blogRepo.findFirstBlogByLanguageAndSegment(languageCode, ENVIRONMENT)
        val entityGraph = entityManager.createEntityGraph(Blog::class.java)
        entityGraph.addAttributeNodes("language")
        entityGraph.addAttributeNodes("blogOwner")
        entityGraph.addAttributeNodes("blogEntries")
        val hints: MutableMap<String, Any> = HashMap()
        hints["javax.persistence.fetchgraph"] = entityGraph
        logger.info("saved")
        val blog2 = entityManager.find(Blog::class.java, blog1?.id, hints)
        assertThat(blog2?.blogEntries?.size).isEqualTo(blog1?.blogEntries?.size)
        logger.info("Blog language: ${blog2.language.language} owner: ${blog2.blogOwner.id} entries: ${blog2.blogEntries}")
    }

    @Test
    fun `read blogs find by language`() {
        logger.info("starting read all test")
        entityManager.clear()
        logger.info("saved")
        val languageCode = LanguageCode("dummy", NB)
        val blogs = blogRepo.findByLanguage(languageCode)
        assertThat(blogs.size).isGreaterThan(0)
        blogs.forEach {
            logger.info("blog: $it")
            assertThat(it.language.code).isEqualTo(NB)
        }
    }
}