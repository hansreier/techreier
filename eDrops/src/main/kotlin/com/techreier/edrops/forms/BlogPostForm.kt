package com.techreier.edrops.forms

data class BlogPostForm(
    var id: Long?, var segment: String = "", var title: String = "", var summary: String = ""
)