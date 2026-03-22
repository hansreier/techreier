package com.techreier.edrops.domain

import java.util.Locale.getDefault

enum class PostState {
    IDEA,
    DRAFT,
    SCHEDULED,
    PUBLISHED,
    BENCHED,
    BACKUP,
    ARCHIVED,
    DEPRECATED,
    UNKNOWN;

    companion object {
        fun find(value: String?): PostState =
            entries.find { it.name.equals(value, ignoreCase = true) } ?: UNKNOWN
    }

    override fun toString(): String =
        name.lowercase().replaceFirstChar { it.titlecase(getDefault()) }

    fun lower() = name.lowercase()

}
