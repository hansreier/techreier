package com.techreier.edrops.forms

import com.techreier.edrops.domain.PostState

data class BlogPostForm(
    var id: Long? = null, var segment: String = "", var state: PostState = PostState.DRAFT,
    var title: String = "", var summary: String = "",
    var content: String = "", var focus: String = "", var preview: String = "", var postLock: Boolean = true
)