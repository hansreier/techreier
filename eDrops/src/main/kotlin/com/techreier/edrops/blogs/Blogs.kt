package com.techreier.edrops.blogs

import com.techreier.edrops.domain.Blog
import com.techreier.edrops.domain.BlogOwner
import com.techreier.edrops.domain.BlogPost
import com.techreier.edrops.domain.Topic
import com.techreier.edrops.exceptions.ParentBlogException

interface Blogs {

    fun no(blog: BlogOwner, topic: Topic): Blog

    fun en(blog: BlogOwner, topic: Topic): Blog
}

fun Blog.addPosts(blogPosts: List<BlogPost>) {
    this.blogPosts.clear()
    blogPosts.forEach {
        if (it.blog != this) throw ParentBlogException(
            "trying to connect post ${it.segment} ${it.title}  " +
                    "owned by ${it.blog.segment} ${it.blog.subject} with ${this.segment} ${this.subject}"
        )
        this.blogPosts.add(it)
    }
}

fun Blog.addPost(blogPost: BlogPost) {
    if (blogPost.blog != this) throw ParentBlogException(
        "trying to connect post ${blogPost.segment} ${blogPost.title}  " +
                "owned by ${blogPost.blog.segment} ${blogPost.blog.subject} with ${this.segment} ${this.subject}"
    )
    this.blogPosts.add(blogPost)
}