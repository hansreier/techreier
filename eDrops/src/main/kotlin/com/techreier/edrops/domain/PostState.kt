package com.techreier.edrops.domain

import java.util.Locale.getDefault

enum class PostState {
    PUBLISHED,
    DRAFT,
    BACKUP,
    ARCHIVED,
    UNKNOWN;

    override fun toString(): String =
        name.lowercase().replaceFirstChar { it.titlecase(getDefault()) }

}
