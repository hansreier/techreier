package com.techreier.edrops.forms

data class BlogEntryForm(
    var id: Long?, var segment: String = "", var title: String = "", var summary: String = ""
)