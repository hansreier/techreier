package com.techreier.edrops.blogs

import com.techreier.edrops.domain.Blog
import com.techreier.edrops.domain.BlogOwner
import com.techreier.edrops.domain.BlogPost
import com.techreier.edrops.domain.Topic

interface Blogs {

fun no(blog: BlogOwner, topic: Topic, blogPostList: MutableList<BlogPost>): Blog

fun en(blog: BlogOwner, topic: Topic, blogPostList: MutableList<BlogPost>): Blog
}