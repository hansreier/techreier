package com.techreier.edrops.forms

data class BlogForm(
    var segment: String = "", var topicKey: String = "", var position: String = "0",
    var subject: String = "", var about: String = "", var postLock: Boolean = false,  var preview: String = ""
)