package com.techreier.edrops.domain

import com.techreier.edrops.model.EnergyValues
import com.techreier.edrops.util.msg
import org.springframework.context.MessageSource


enum class PostState(state: String) {
    PUBLISHED("published"),
    DRAFT("draft"),
    BACKUP("backup"),
    ARCHIVED("archived"),
    UNKNOWN("unknown");
    override fun toString(): String = name.lowercase()
}
