package com.techreier.edrops.data.blogs

import com.techreier.edrops.domain.Blog
import com.techreier.edrops.domain.BlogOwner
import com.techreier.edrops.domain.BlogPost
import com.techreier.edrops.domain.Topic
import com.techreier.edrops.exceptions.ParentBlogException

interface Blogs {

    fun no(blogOwner: BlogOwner, topic: Topic): Blog

    fun en(blogOwner: BlogOwner, topic: Topic): Blog
}

fun Blog.addPosts(vararg factories: (Blog) -> BlogPost): Blog {
    this.blogPosts.clear()
    factories.forEach { factory ->
        val post = factory(this)
        if (post.blog != this) throw ParentBlogException(
            "trying to connect post ${post.segment} ${post.title}  " +
                    "owned by ${post.blog.segment} ${post.blog.subject} with ${this.segment} ${this.subject}")
        this.blogPosts.add(post)
        this.blogPosts.sortByDescending { it.changed }
    }
    return this
}
