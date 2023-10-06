package com.techreier.edrops.util

import com.techreier.edrops.domain.English
import com.techreier.edrops.domain.Norwegian

const val MARKDOWN_EXT = ".md"
const val HOME ="home"

/**
 * No database needed for rendering docs saved as markdown files.
 * Documents read directly from disk (including definition for drop down menu)
 * Tag is required for file name
 * If no value of subject, tag is used to pick up text in resource
 * Language is part of file name if ext is set to true
 */
object Docs {
    private val doc = listOf(
        Doc("goals",  Norwegian, "Mål"),
        Doc("goals",  English, "Goals"),
        Doc("readme", Norwegian, "Les meg (engelsk)", false),
        Doc("readme", English, null, false),
        Doc("tech", Norwegian, "Om teknologi (engelsk)",false),
        Doc("tech", English, "Technology",false),
        Doc("markdown", Norwegian, "Markdown (engelsk)", false),
        Doc( "markdown", English, "Markdown", false),
        Doc("databases", Norwegian, "Databaser (engelsk)", false),
        Doc("databases", English, "Databases", false)
    )

    fun getDoc(blogId: Long): Doc {
        return doc[blogId.toInt()]
    }

    fun getDocId(tag: String, langCode: String): Int {
        return doc.indexOfFirst { (it.language.code == langCode) && (it.tag == tag) }
    }


    fun getDocs(langCode: String): List<Doc> {
        return doc.filter { (it.language.code == langCode)  }
    }
}

