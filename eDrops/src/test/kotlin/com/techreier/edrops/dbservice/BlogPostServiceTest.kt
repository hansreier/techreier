package com.techreier.edrops.dbservice


import com.techreier.edrops.config.logger
import com.techreier.edrops.domain.PostState
import com.techreier.edrops.forms.BlogPostForm
import com.techreier.edrops.repository.TestBase
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.Instant

@SpringBootTest
class BlogPostServiceTest : TestBase() {

    @Autowired
    private lateinit var postService: BlogPostService

    @Test
    fun newPostTest() {

        // BlogPost without BlogText
        val segment1 = "test"
        val timestamp = Instant.now()
        val form1 = BlogPostForm(segment = segment1, summary = "summary")
        postService.save( blogPrincipal, null, form1, timestamp)
        val (post1, text1) = postService.readBlogPost(blogId, segment1, PostState.IDEA)
        assertNotNull(post1)
        assertNull(text1)
        assertEquals("summary", post1.summary)

        // BlogPost with BlogText
        val segment2 = "test2"
        val form2 = BlogPostForm(segment = segment2, content = "text")
        postService.save(blogPrincipal, null, form2, timestamp)
        val (post2, text2) = postService.readBlogPost(blogId, segment2, PostState.IDEA)
        assertNotNull(post2)
        assertNotNull(text2)
        logger.debug("blogId=${blog.id} id: ${post2.id} segment: ${post2.segment} state: ${post2.state}")
        assertEquals("text", text2.text)
    }


    @Test
    fun existingPostTest() {

        //BlogPost without BlogText
        val timestamp = Instant.now()
        val form1 = BlogPostForm(segment = blogPost.segment, summary = "summary")
        postService.save(blogPrincipal, blogPostId, form1, timestamp)
        val (post1, text1) = postService.readBlogPost(blogId, blogPost.segment, PostState.IDEA)
        assertNotNull(post1)
        assertNotNull(post1.id)
        assertEquals(blogPostId, post1.id)
        assertNull(text1)
        assertEquals("summary", post1.summary)

        //BlogPost with BlogText
        val form2 = BlogPostForm(segment = blogPost.segment, content = "text")
        postService.save(blogPrincipal, blogPostId, form2, timestamp)
        val (post2, text2) = postService.readBlogPost(blogId, blogPost.segment, PostState.IDEA)
        assertNotNull(post2)
        assertNotNull(post2.id)
        assertEquals(blogPostId, post2.id)
        assertNotNull(text2)
        assertEquals("text", text2.text)

        //BlogPost with empty BlogText
        val form3 = BlogPostForm(segment = blogPost.segment, content = " ")
        postService.save(blogPrincipal, blogPostId, form3, timestamp)
        val (post3, text3) = postService.readBlogPost(blogId, blogPost.segment, PostState.IDEA)
        assertNotNull(post3)
        assertNotNull(post3.id)
        assertEquals(blogPostId, post3.id)
        assertNull(text3)
    }

    @Test
    fun isDuplicateTest() {
        val state = PostState.find(blogPost.state, true)
        assertFalse(postService.duplicate(blogPost.segment, blogId, state, blogPostId))
        assertTrue(postService.duplicate(blogPost.segment, blogId, state, null))

        val form = BlogPostForm(segment = blogPost.segment, state = PostState.DEPRECATED)
        postService.save(blogPrincipal, null, form, Instant.now())
        assertTrue(postService.duplicate(blogPost.segment, blogId, PostState.DEPRECATED, blogPostId))
    }

    @Test
    fun findAndDeleteTest() { //Save function does not prevent duplicate, delete all duplicates
        val state = PostState.find(blogPost.state, true)
        val form = BlogPostForm(segment = blogPost.segment, state = state)
        val id = postService.save(blogPrincipal, null, form, Instant.now())
        val summaries = postService.findSummaries(blogPost.segment, blogId, state)
        assertThat(summaries.size).isEqualTo(2)
        assertThat(summaries.map{it.id}).containsAll(listOf(id, blogPostId))
        postService.delete(blogId, summaries.map {it.id})
        assertNull(postRepo.findById(summaries.first().id).orElse(null))
        assertNull(postRepo.findById(summaries[1].id).orElse(null))
    }

    // TODO ReierAsk add more functionality due to added functionality connected with blogPrincipal and changed parent blog
}