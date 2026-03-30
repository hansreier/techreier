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

    // Observe: When called from database, boolean should be true, else false
    companion object {
        fun find(value: String?, ignoreCase: Boolean = false ): PostState =
            entries.find { it.name.equals(value?.uppercase(), ignoreCase = ignoreCase) } ?: UNKNOWN
    }

    override fun toString(): String =
        name.lowercase().replaceFirstChar { it.titlecase(getDefault()) }

    fun lower() = name.lowercase()

}
