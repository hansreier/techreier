package com.techreier.edrops.dto

import com.techreier.edrops.repository.projections.IBlogPost
import com.techreier.edrops.repository.projections.IBlogText

data class PostWithText(
        val post: IBlogPost?,
        val text: IBlogText?,
    )
