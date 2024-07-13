package com.techreier.edrops.util

import com.techreier.edrops.domain.English
import com.techreier.edrops.domain.Norwegian
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

const val MARKDOWN_EXT = ".md"
const val ILLEGAL_PATH = "Illegal path"

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
        Doc("elpower",  Norwegian, "Elkraft i Norge"),
        Doc("elpower",  English, "Elpower in Norway"),
        Doc("manifest",  Norwegian, "Strøm manifest"),
        Doc("manifest",  English, "Electrical power manifest"),
        Doc("elprice",  Norwegian, "Strøm(pris)krisen", false),
        Doc("elcrazy",  Norwegian, "To år med elgalskap", false),
        Doc("ringsaker", Norwegian, "Kraft og hytter i Ringsaker" , false),
        Doc("windpower", Norwegian, "Myter om vindkraft", false),
    )

   val about = arrayOf(
        Doc("reier", Norwegian,"Meg"),
        Doc( "reier", English, "Me"),
        Doc("website",  Norwegian, "Nettsted"),
        Doc("website",  English, "Website"),
        Doc("energy",  Norwegian, "Energi"),
        Doc("energy",  English, "Energy"),
        Doc("energylinks",  Norwegian, "Energi linker"),
        Doc("energylinks",  English, "Energy links"),
        Doc("readme", Norwegian, "Prosjektet", false),
        Doc("readme", English, "Project", false),
        Doc("tech", Norwegian, "Teknologi",false),
        Doc("tech", English, "Technology",false),
        Doc("markdown", Norwegian, "Markdown", false),
        Doc( "markdown", English, "Markdown", false),
        Doc("databases", Norwegian, "Databaser", false),
        Doc("databases", English, "Databases", false),
        Doc("hosting", Norwegian, "Mitt web hotell (engelsk)", false),
        Doc("hosting", English, "My web host", false)
    )

    val collatz = arrayOf(
        Doc("collatz", Norwegian,"Collatz") ,
        Doc("collatz", English,"Collatz")
    )

    // Find the first Doc index that matches language code and eventually nonnull tag
    fun getDocIndex(docs: Array<Doc>, languageCode: String, tag: String? = null): Int {
        val usedCode = usedLanguageCode(languageCode)
        val docIndex =  docs.indexOfFirst { (it.language.code == usedCode) && (tag == it.tag || tag == null) }
        if (docIndex < 0) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, ILLEGAL_PATH)
        }
        return docIndex
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

