package com.techreier.edrops.domain

import com.techreier.edrops.exceptions.StateNotFoundException
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

    // Observe: When called from database, be nice and access everything
    companion object {
        fun find(value: String?, fromDB: Boolean): PostState {

        val match = entries.find{
            val stringToCheck = if (fromDB) it.name else it.name.lowercase()
            stringToCheck.equals(other = value, ignoreCase = fromDB)
        }
            return when {
                match != null -> match
                fromDB -> UNKNOWN
                else -> throw StateNotFoundException("ugyldig path / tilstand $value")
            }
        }
    }

    override fun toString(): String =
        name.lowercase().replaceFirstChar { it.titlecase(getDefault()) }

    fun lower() = name.lowercase()

}
