package com.techreier.edrops.repository

import com.techreier.edrops.config.logger
import com.techreier.edrops.dbservice.BlogPostService
import com.techreier.edrops.domain.BlogPost
import com.techreier.edrops.domain.PostState
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.temporal.ChronoUnit

@SpringBootTest
@Transactional
class BlogPostTest : TestBase() {

    @Autowired
    private lateinit var blogPostService: BlogPostService

    @Test
    fun `basic CRUD checks`() {
        logger.info("Basic crud test start create")
        var blogPostService1 = BlogPost(
            Instant.now().truncatedTo(ChronoUnit.SECONDS),

           // ZonedDateTime.now(ZoneOffset.of(DEFAULT_TIMEZONE)).truncatedTo(ChronoUnit.SECONDS).toInstant(),
            "Katt", "Pus", "Min katt er huskatt", blog
        )
        blogPostService1 = postRepo.saveAndFlush(blogPostService1)
        blog.blogPosts.add(blogPostService1)
        assertNotNull(blogPostService1)
        assertNotNull(blogPostService1.id)
        val blogPost1id = blogPostService1.id!!
        entityManager.clear()
        logger.info("Basic crud test start read")
        val blogPost2: BlogPost? = postRepo.findById(blogPost1id).orElse(null)
        assertNotNull(blogPost2)
        assertNotNull(blogPost2!!.id)
        assertEquals(blogPostService1.changed, blogPost2.changed)
        logger.info("Basic crud test start update")
        blogPostService1.title = "Pusur"
        postRepo.save(blogPostService1)
        val blogList = postRepo.findByTitle("Pusur", PostState.PUBLISHED.name)
        assertThat(blogList.size).isEqualTo(1)
        assertNotNull(blogList[0])
        assertNotNull(blogList[0].id)
        assertEquals("Pusur", blogList[0].title)
        logger.info("Basic crud test start delete")
        postRepo.delete(blogPostService1)
        val notFound: BlogPost? = postRepo.findById(blogPost1id).orElse(null)
        assertNull(notFound)
    }
}


