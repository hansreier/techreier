package com.techreier.edrops.forms

data class BlogForm(
    var id: Long?, var segment: String = "", var topicKey: String = "", var position: Int = 0,
    var subject: String = "", var about: String = ""
)