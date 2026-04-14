package com.techreier.edrops.dbservice


import com.techreier.edrops.domain.PostState
import com.techreier.edrops.forms.BlogPostForm
import com.techreier.edrops.repository.TestBase
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@SpringBootTest
@Transactional
class BlogPostServiceTest : TestBase() {

    @Autowired
    private lateinit var postService: BlogPostService

    @Test
    fun newPostTest() {

        // BlogPost without BlogText
        val segment1 = "test"
        val timestamp = Instant.now()
        val form1 = BlogPostForm(segment = segment1, summary = "summary")
        postService.save(blogId, null, form1, timestamp)
        val (post1, text1) = postService.readBlogPost(blogId, segment1, PostState.IDEA)
        assertNotNull(post1)
        assertNull(text1)
        assertEquals("summary", post1.summary)

        // BlogPost with BlogText
        val segment2 = "test2"
        val form2 = BlogPostForm(segment = segment2, content = "text")
        postService.save(blogId, null, form2, timestamp)
        val (post2, text2) = postService.readBlogPost(blogId, segment2, PostState.IDEA)
        assertNotNull(post2)
        assertNotNull(text2)
        assertEquals("text", text2.text)
    }


    @Test
    fun existingPostTest() {

        //BlogPost without BlogText
        val timestamp = Instant.now()
        val form1 = BlogPostForm(segment = blogPost.segment, summary = "summary")
        postService.save(blogId, blogPostId, form1, timestamp)
        val (post1, text1) = postService.readBlogPost(blogId, blogPost.segment, PostState.IDEA)
        assertNotNull(post1)
        assertNotNull(post1.id)
        assertEquals(blogPostId, post1.id)
        assertNull(text1)
        assertEquals("summary", post1.summary)

        //BlogPost with BlogText
        val form2 = BlogPostForm(segment = blogPost.segment, content = "text")
        postService.save(blogId, blogPostId, form2, timestamp)
        val (post2, text2) = postService.readBlogPost(blogId, blogPost.segment, PostState.IDEA)
        assertNotNull(post2)
        assertNotNull(post2.id)
        assertEquals(blogPostId, post2.id)
        assertNotNull(text2)
        assertEquals("text", text2.text)

        //BlogPost with empty BlogText
        val form3 = BlogPostForm(segment = blogPost.segment, content = " ")
        postService.save(blogId, blogPostId, form3, timestamp)
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
        postService.save(blogId, null, form, Instant.now())
        assertTrue(postService.duplicate(blogPost.segment, blogId, PostState.DEPRECATED, blogPostId))
    }

    @Test
    fun findAndDeleteTest() { //Save function does not prevent duplicate, delete all that is duplicate
        val state = PostState.find(blogPost.state, true)
        val form = BlogPostForm(segment = blogPost.segment, state = state)
        val id = postService.save(blogId, null, form, Instant.now())
        val ids = postService.findIds(blogPost.segment,blogId, state)
        assertThat(ids.size).isEqualTo(2)
        assertThat(ids).containsAll(listOf(id, blogPostId))
        postService.delete(blogId, ids )
        assertNull(postRepo.findById(ids.first()).orElse(null))
        assertNull(postRepo.findById(ids[1]).orElse(null))
    }
}