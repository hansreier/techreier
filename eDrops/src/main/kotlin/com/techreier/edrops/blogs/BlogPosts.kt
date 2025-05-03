package com.techreier.edrops.blogs

import com.techreier.edrops.domain.Blog
import com.techreier.edrops.domain.BlogPost

interface BlogPosts {

fun no(blog: Blog): BlogPost

fun en(blog: Blog): BlogPost
}