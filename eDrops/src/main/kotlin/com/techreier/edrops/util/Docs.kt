package com.techreier.edrops.util

import com.techreier.edrops.domain.English
import com.techreier.edrops.domain.Norwegian

const val MARKDOWN_EXT = ".md"

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
    val home = arrayOf(
        Doc("elpower",  Norwegian, "Elkraft i Norge", false),
        Doc("elpower",  English, "Elpower in Norway (Norwegian) ", false),
        Doc("manifest",  Norwegian, "Strøm manifest", false),
        Doc("manifest",  English, "Electrical power manifest (Norwegian)", false),
        Doc("elcrazy",  Norwegian, "To år med elgalskap", false),
        Doc("elcrazy",  English, "Two years of elcraziness (Norwegian)", false),
        Doc("ringsaker",  Norwegian, "Kraft og hytter i Ringsaker" , false),
        Doc("ringsaker",     English, "Power and cabins in Ringsaker (Norwegian)", false)
    )

   val about = arrayOf(
        Doc("reier", Norwegian,"Meg"),
        Doc( "reier", English, "Me"),
        Doc("website",  Norwegian, "Nettsted"),
        Doc("website",  English, "Website"),
        Doc("energy",  Norwegian, "Energi", false),
        Doc("energy",  English, "Energy (Norwegian) ", false),
        Doc("readme", Norwegian, "Prosjektet (engelsk)", false),
        Doc("readme", English, "Project", false),
        Doc("tech", Norwegian, "Teknologi (engelsk)",false),
        Doc("tech", English, "Technology",false),
        Doc("markdown", Norwegian, "Markdown (engelsk)", false),
        Doc( "markdown", English, "Markdown", false),
        Doc("databases", Norwegian, "Databaser (engelsk)", false),
        Doc("databases", English, "Databases", false),
        Doc("hosting", Norwegian, "Mitt web hotell (engelsk)", false),
        Doc("hosting", English, "My web host", false)
    )

    // Find the first Doc index that matches language code and eventually nonnull tag
    fun getDocIndex(docs: Array<Doc>, languageCode: String, tag: String? = null): Int {
        val usedCode = usedLanguageCode(languageCode)
        return docs.indexOfFirst { (it.language.code == usedCode) && (tag == it.tag || tag == null) }
    }

    fun getDocs(docs: Array<Doc>, languageCode: String): List<Doc> {
        val usedCode = usedLanguageCode(languageCode)
        return docs.filter { (it.language.code == usedCode)  }
    }

    // Rule for returning language code used in this project from unknown codes.
   fun usedLanguageCode(languageCode: String ): String {
        return  if (languageCode in listOf("nn","no","nb")) "nb" else "en"
    }

}

