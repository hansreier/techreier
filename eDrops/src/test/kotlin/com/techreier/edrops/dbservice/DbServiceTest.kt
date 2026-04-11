package com.techreier.edrops.dbservice


import com.techreier.edrops.data.NB
import com.techreier.edrops.data.TOPIC_DEFAULT
import com.techreier.edrops.dto.BlogPrincipal
import com.techreier.edrops.dto.BlogWithPosts
import com.techreier.edrops.forms.BlogForm
import com.techreier.edrops.repository.TestBase
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@SpringBootTest
@Transactional
class DbServiceTest : TestBase() {

    @Autowired
    private lateinit var blogService: BlogService

    @Test
    fun newBlogTest() {
        val segment = "test"
        val langCode = NB
        val topic = TOPIC_DEFAULT
        val timestamp = Instant.now()
        val blogForm = BlogForm(segment, topic)
        val blogPrincipal = BlogPrincipal(blogOwnerId, null, langCode)

        blogService.save(blogPrincipal, blogForm, timestamp)

        val blogWithPosts1: BlogWithPosts? = blogService.readBlog(segment, langCode, false, true)
        assertNotNull(blogWithPosts1)
        val blog1 = blogWithPosts1.blog
        assertNotNull(blog1)
        assertNull(blogWithPosts1.posts)

        val blogWithPosts2: BlogWithPosts? = blogService.readBlog(segment, langCode, true, true)
        assertNotNull(blogWithPosts2)
        val blog2 = blogWithPosts2.blog
        assertNotNull(blog2)
        assertThat(blogWithPosts2.posts).isEmpty()
    }

    @Test
    fun existingBlogTest() {
        val segment = "test"
        val timestamp = Instant.now()
        val langCode = blog.topic.language.code
        val topicKey = blog.topic.topicKey
        val blogForm = BlogForm(segment, topicKey, "1","test","about test" )
        val blogPrincipal = BlogPrincipal(blogOwnerId, blogId, langCode)

        blogService.save(blogPrincipal, blogForm, timestamp)

        val blogWithPosts: BlogWithPosts? = blogService.readBlog(segment, langCode, true, true)
        assertNotNull(blogWithPosts)
        val blogFound = blogWithPosts.blog
        assertNotNull(blogFound)
        assertEquals(segment, blogFound.segment)
        assertEquals(blogForm.position.toInt(), blogFound.pos)
        assertEquals(blogForm.subject, blogFound.subject)
        assertEquals(blogForm.about, blogFound.about)
        assertThat(blogWithPosts.posts).size().isEqualTo(blog.blogPosts.size)
    }

}