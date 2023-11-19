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
   val doc = arrayOf(
        Doc("goals",  Norwegian, "Mål"),
        Doc("goals",  English, "Goals"),
        Doc("reier", Norwegian,"Meg"),
        Doc( "reier", English, "Me"),
        Doc("readme", Norwegian, "Les meg (engelsk)", false),
        Doc("readme", English, null, false),
        Doc("tech", Norwegian, "Om teknologi (engelsk)",false),
        Doc("tech", English, "Technology",false),
        Doc("markdown", Norwegian, "Markdown (engelsk)", false),
        Doc( "markdown", English, "Markdown", false),
        Doc("databases", Norwegian, "Databaser (engelsk)", false),
        Doc("databases", English, "Databases", false)
    )

    // Find the first Doc index that matches language code and eventually nonnull tag
    fun getDocIndex(languageCode: String, tag: String? = null): Int {
        val usedCode = usedLanguageCode(languageCode)
        return doc.indexOfFirst { (it.language.code == usedCode) && (tag == it.tag || tag == null) }
    }

    fun getDocs(languageCode: String): List<Doc> {
        val usedCode = usedLanguageCode(languageCode)
        return doc.filter { (it.language.code == usedCode)  }
    }

    // Rule for returning language code used in this project from unknown codes.
   fun usedLanguageCode(languageCode: String ): String {
        return  if (languageCode in listOf("nn","no","nb")) "nb" else "en"
    }

}

