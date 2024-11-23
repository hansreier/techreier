package com.techreier.edrops.repository

import com.techreier.edrops.config.logger
import com.techreier.edrops.domain.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.annotation.Transactional


@ExtendWith(SpringExtension::class)
@SpringBootTest
@Transactional
class TestBlog : TestBase() {
    @Test
    fun `test findAll`() {
        val instances = blogRepo.findAll()
        assertThat(instances.size).isEqualTo(blogData.noOfBlogs)
    }

    @Test
    fun `cascade delete test`() {
        logger.info("starting transactional test")
        val blog1 = blogOwner.blogs?.first()
        val blogEntry1 = blog1?.blogEntries?.first()
        logger.info("blogEntry: $blogEntry1")
        val blogEntrySaved = entryRepo.findByIdOrNull(blogEntry1?.id)
        assertThat(blogEntrySaved?.id).isEqualTo(blogEntry1?.id)
        blogRepo.delete(blog1!!)
        logger.info("Deleted")
        val blogEntryDeleted = entryRepo.findByIdOrNull(blogEntry1?.id)
        assertThat(blogEntryDeleted).isNull()
        logger.info("completed")
    }

    @Test
    fun `read blog with JPQL test`() {
        logger.info("starting transactional test")
        val query =
            entityManager.createQuery("SELECT DISTINCT b FROM Blog b INNER JOIN FETCH b.blogEntries WHERE b.subject = ?1 ")
        val blog = query.setParameter(1, SUBJECT1).singleResult as Blog
        logger.info("blog: $blog")
        assertThat(blog.language.language).isEqualTo(NORWEGIAN)
        assertThat(blog.segment).isEqualTo(ENVIRONMENT)
    }

    @Test
    fun `read blog simple test`() {
        val blog = blogRepo.findFirstBlogByLanguageAndSegment(Norwegian, ENVIRONMENT)
        assertThat(blog).isNotNull
        assertThat(blog?.language?.code).isEqualTo(NB)
        assertThat(blog?.language?.language).isEqualTo(NORWEGIAN)
        assertThat(blog?.segment).isEqualTo(ENVIRONMENT)
        logger.info("blog: $blog ${blog?.blogEntries?.size}")
        assertThat(blog?.blogEntries?.size).isEqualTo(blogData.noOfBlog1Entries)
    }

    @Test
    //Using JPQL more efficient, only one SQL statement
    //https://www.baeldung.com/spring-data-jpa-named-entity-graphs
    fun `read all with findAll test`() {
        logger.info("starting read all test")
        val blog = blogRepo.findAll()
        logger.info("blogs: $blog noOfBlogs: ${blog.size}")
        assertThat(blog.size).isEqualTo(blogData.noOfBlogs)
        blog.forEach {
            val entries = it.blogEntries
            logger.info("my entries: $entries")
        }
    }

    @Test
    //https://www.baeldung.com/jpa-entity-graph
    fun `read with entityManager find manual entityGraph`() {
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
        val blog = entityManager.find(Blog::class.java, blog1?.id, hints)
        assertThat(blog?.blogEntries?.size).isEqualTo(blog1?.blogEntries?.size)
        logger.info("Blog language: ${blog.language.language} owner: ${blog.blogOwner.id} entries: ${blog.blogEntries}")
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