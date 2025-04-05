package com.techreier.edrops.repository

import com.techreier.edrops.config.logger
import com.techreier.edrops.domain.*
import jakarta.persistence.Subgraph
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
class BlogTest : TestBase() {
    // Do not use this Kotlin extension, does not read JPA annotations, generates more SQL statements
    @Test
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
        val blog1: Blog? = blogRepo.findById(blogId).orElse(null)
        assertNotNull(blog1)
        assertEquals(this.blog.id, blog1!!.id)
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
        val blog1 = blogOwner.blogs.first()
        val blogPost1 = blog1.blogPosts.first()
        val blogPost1id = blogPost1.id!!
        assertNotNull(blogPost1)
        logger.info("blogPost: $blogPost1")
        val blogPostSaved: BlogPost? = postRepo.findById(blogPost1id).orElse(null)
        assertNotNull(blogPostSaved)
        assertThat(blogPostSaved!!.id).isEqualTo(blogPost1id)
        blogRepo.delete(blog1)
        logger.info("Deleted")
        val blogPostDeleted: BlogPost? = postRepo.findById(blogPost1id).orElse(null)
        assertThat(blogPostDeleted).isNull()
        logger.info("completed")
    }

    @Test
    fun `read blog with JPQL`() {
        logger.info("starting transactional test")
        val query =
            entityManager.createQuery("SELECT DISTINCT b FROM Blog b INNER JOIN FETCH b.blogPosts WHERE b.subject = ?1 ")
        val blog = query.setParameter(1, SUBJECT1).singleResult as Blog
        logger.info("blog: $blog")
        assertThat(blog.topic.language.language).isEqualTo(NORWEGIAN)
        assertThat(blog.segment).isEqualTo(BSEG_ENVIRONMENT)
    }

    @Test
    fun `read blog by language and segment`() {
        logger.info("starting read blog by language and segment")
        val blog1 = blogRepo.findByTopicLanguageCodeAndSegment(NB, BSEG_ENVIRONMENT)
        assertThat(blog1).isNotNull
        assertThat(blog1!!.topic.language.code).isEqualTo(NB)
        assertThat(blog1.topic.language.language).isEqualTo(NORWEGIAN)
        assertThat(blog1.segment).isEqualTo(BSEG_ENVIRONMENT)
        logger.info("blog: $blog1 ${blog1.blogPosts.size}")
    }

    // Using JPQL more efficient, only one SQL statement
    // https://www.baeldung.com/spring-data-jpa-named-entity-graphs
    @Test
    fun `read all with findAll`() {
        logger.info("starting read all test")
        val blogs = blogRepo.findAll()
        logger.info("blogs: $blogs noOfBlogs: ${blogs.size}")
        assertThat(blogs.size).isEqualTo(noOfBlogs)
        blogs.forEach {
            val posts = it.blogPosts
            logger.info("my posts: $posts")
        }
    }

    // https://www.baeldung.com/jpa-entity-graph
    @Test
    fun `read with manual entityGraph`() {
        logger.info("starting read all test")
        val blog1 = blogRepo.findByTopicLanguageCodeAndSegment(NB, BSEG_ENVIRONMENT)
        val entityGraph = entityManager.createEntityGraph(Blog::class.java)
        val topicGraph: Subgraph<Topic> = entityGraph.addSubgraph("topic")
        topicGraph.addAttributeNodes("language")
        entityGraph.addAttributeNodes("blogOwner")
        entityGraph.addAttributeNodes("blogPosts")
        val hints: MutableMap<String, Any> = HashMap()
        hints["javax.persistence.fetchgraph"] = entityGraph
        logger.info("saved")
        val blog2 = entityManager.find(Blog::class.java, blog1?.id, hints)
        assertThat(blog2?.blogPosts?.size).isEqualTo(blog1?.blogPosts?.size)
        logger.info("Blog language: ${blog2.topic.language.language} owner: ${blog2.blogOwner.id} posts: ${blog2.blogPosts}")
    }

    @Test
    fun `read blogs find by language`() {
        logger.info("starting read all test")
        entityManager.clear()
        logger.info("saved")
        val blogs = blogRepo.findByTopicLanguageCode(NB)
        assertThat(blogs.size).isGreaterThan(0)
        blogs.forEach {
            logger.info("blog: $it")
            assertThat(it.topic.language.code).isEqualTo(NB)
        }
    }
}
