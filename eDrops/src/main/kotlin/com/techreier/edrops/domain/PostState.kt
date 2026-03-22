package com.techreier.edrops.domain

import java.util.Locale.getDefault

enum class PostState {
    PUBLISHED,
    DRAFT,
    BACKUP,
    ARCHIVED,
    UNKNOWN;

    companion object {
        fun find(value: String?): PostState =
            entries.find { it.name.equals(value, ignoreCase = true) } ?: UNKNOWN
    }

    override fun toString(): String =
        name.lowercase().replaceFirstChar { it.titlecase(getDefault()) }

    fun lower() = name.lowercase()

}
