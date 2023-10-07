package com.techreier.edrops.util

import com.techreier.edrops.domain.English
import com.techreier.edrops.domain.Norwegian

const val MARKDOWN_EXT = ".md"
const val HOME ="home"

/**
 * No database needed for rendering docs saved as markdown files.
 * Documents read directly from disk.
 * Documents are fetch from drop down menu
 * TODO: Enable usage of multiple drop down menus
 * The tag is the file name without extension
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

    fun getDoc(docIndex: Int): Doc {
        return doc[docIndex]
    }

    // Find the first Doc index that matches language code and eventually nonnull tag
    fun getDocIndex(tag: String?, languageCode: String): Int {
        return doc.indexOfFirst { (it.language.code == languageCode) && (tag == it.tag || tag == null) }
    }

    fun getDocs(languageCode: String): List<Doc> {
        return doc.filter { (it.language.code == languageCode)  }
    }
}

