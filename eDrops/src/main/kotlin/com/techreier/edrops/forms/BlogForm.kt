package com.techreier.edrops.forms

data class BlogForm(
    var id: Long? = null, var segment: String = "", var topicKey: String = "", var position: String = "0",
    var subject: String = "", var about: String = "", var postLock: Boolean = true
)