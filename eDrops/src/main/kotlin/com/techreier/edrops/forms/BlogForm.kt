package com.techreier.edrops.forms

data class BlogForm(
    var id: Long?, var segment: String = "", var topic: String = "",
    var subject: String = "", var about: String = "", var summary: String = "",
)