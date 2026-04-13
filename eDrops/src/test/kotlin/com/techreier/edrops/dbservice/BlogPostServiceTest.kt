package com.techreier.edrops.dbservice


import com.techreier.edrops.data.EN
import com.techreier.edrops.data.NB
import com.techreier.edrops.data.TOPIC_DEFAULT
import com.techreier.edrops.domain.PostState
import com.techreier.edrops.dto.BlogPrincipal
import com.techreier.edrops.dto.BlogWithPosts
import com.techreier.edrops.exceptions.DuplicateBlogException
import com.techreier.edrops.exceptions.TopicNotFoundException
import com.techreier.edrops.forms.BlogForm
import com.techreier.edrops.forms.BlogPostForm
import com.techreier.edrops.repository.TestBase
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.time.Instant

@SpringBootTest
@Transactional
class BlogPostServiceTest : TestBase() {

    @Autowired
    private lateinit var postService: BlogPostService

    @Test
    fun newPostTest() {
        val segment = "test"
        val timestamp = Instant.now()
        val form = BlogPostForm(segment)

        // Happy day case
        postService.save(blogId, null , form, timestamp)

        // Read blogpost and blogtext
        val (post, text) = postService.readBlogPost(blogId, segment, PostState.IDEA )
        assertNotNull(post)
        assertNull(text)

    }


    /*
    @Test
    fun existingBlogTest() {
        val segment = "test"
        val timestamp = Instant.now()
        val langCode = blog.topic.language.code
        val topicKey = blog.topic.topicKey
        val blogForm = BlogForm(segment, topicKey, "1", "test", "about test")
        val blogPrincipal = BlogPrincipal(blogOwnerId, blogId, langCode)

        // Topic not found case
        val e = assertThrows<TopicNotFoundException> {
            blogService.save(blogPrincipal, BlogForm(), timestamp)
        }
        assertThat(e.message!!.contains("topic"))

        // Happy day case
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

    @Test
    fun duplicateBlogTest() {
        val segment = blog.segment
        val timestamp = Instant.now()
        val langCode = blog.topic.language.code
        val topicKey = blog.topic.topicKey
        val blogForm = BlogForm(segment, topicKey, "1", "test", "about test")
        val blogPrincipal = BlogPrincipal(blogOwnerId, null, langCode)
        blogService.save(blogPrincipal, blogForm, timestamp) //No duplicate check on save
        assertThrows<DuplicateBlogException> {
            blogService.readBlog(segment, langCode, true, true)
        }
        assertThrows<DuplicateBlogException> {
            blogService.findId(segment, blogOwnerId, langCode)
        }
    }

    @Test
    fun isDuplicateTest() {
        val blogPrincipal = BlogPrincipal(blogOwnerId, null, blog.topic.language.code)
        assertTrue(blogService.duplicate(blog.segment, blogPrincipal)) //Duplicate check function, use before save
    }

    @Test
    fun notFoundSaveBlogTest() {
        val blogPrincipal = BlogPrincipal(blogOwnerId, -1, NB)
        val topicKey = blog.topic.topicKey
        val blogForm = BlogForm("rubbish", topicKey)
        val e = assertThrows<ResponseStatusException> {
            blogService.save(blogPrincipal, blogForm, Instant.now())
        }
        assertThat(e.message).contains("404")
    }

    @Test
    fun notFoundReadBlogTest() {
        val blogWithPosts = blogService.readBlog("tull", NB, false, true)
        assertNull(blogWithPosts)
    }

    @Test
    fun findAndDeleteTest() {
        val segment = blog.segment
        val langCode = blog.topic.language.code
        val foundBlogId = blogService.findId(segment, blogOwnerId, langCode)
        assertEquals(blogId, foundBlogId)
        blogService.delete(null)
        blogService.delete(-1)
        assertNotNull(blogRepo.findById(blogId).orElse(null))
        blogService.delete(foundBlogId)
        assertNull(blogRepo.findById(blogId).orElse(null))
    } */
}