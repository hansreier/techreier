package com.techreier.edrops.forms

data class BlogPostForm(
    var id: Long? = null, var segment: String = "", var title: String = "", var summary: String = ""
)