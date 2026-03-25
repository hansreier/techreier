package com.techreier.edrops.dto

import com.techreier.edrops.domain.BlogOwner

data class BlogRef(
    val blogOwner: BlogOwner,
    val ownerId: Long,
    val blogId: Long?,
    val langCode: String
)
