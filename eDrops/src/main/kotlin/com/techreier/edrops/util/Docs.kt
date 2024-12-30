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
 * The segment is the file name without extension
 * If no value of subject, segment is used to pick up text in resource
 * Language is part of file name if ext is set to true
 */
object Docs {
    val home = arrayOf(
        Doc("energy",  Norwegian, "Energi i Norge"),
        Doc("energy",  English, "Energy in Norway"),
        Doc("elpower",  Norwegian, "Elkraft i Norge"),
        Doc("elpower",  English, "Electrical power in Norway"),
        Doc("manifest",  Norwegian, "Strøm manifest"),
        Doc("manifest",  English, "Electrical power manifest"),
        Doc("elprice",  Norwegian, "Strøm(pris)krisen", false),
        Doc("elcrazy",  Norwegian, "To år med elgalskap", false),
        Doc("ringsaker", Norwegian, "Kraft og hytter i Ringsaker" , false),
        Doc("windpower", Norwegian, "Myter om vindkraft", false),
        Doc("energylinks",  Norwegian, "Energi linker"),
        Doc("energylinks",  English, "Energy links"),
        Doc("ai2084", Norwegian, "AI paranoia", false),
        Doc("homeoffice", Norwegian, "Om hjemmekontor"),
        Doc("homeoffice", English, "About home office"),
    )

   val about = arrayOf(
        Doc("reier", Norwegian,"Meg"),
        Doc( "reier", English, "Me"),
        Doc("links",  Norwegian, "Mine linker"),
        Doc("links",  English, "My links"),
        Doc("website",  Norwegian, "Nettsted"),
        Doc("website",  English, "Website"),
        Doc("readme", Norwegian, "Prosjektet", false),
        Doc("readme", English, "Project", false),
        Doc("tech", Norwegian, "Teknologi",false),
        Doc("tech", English, "Technology",false),
        Doc("maintainable", Norwegian, "Vedlikeholdbarhet"),
        Doc("maintainable", English, "Maintainability"),
        Doc("greenit", Norwegian, "Grønn IKT"),
        Doc("greenit", English, "Green ICT"),
        Doc("greencode", Norwegian, "Grønn koding"),
        Doc("greencode", English, "Green coding"),
        Doc("markdown", Norwegian, "Markdown", false),
        Doc( "markdown", English, "Markdown", false),
        Doc("databases", Norwegian, "Databaser", false),
        Doc("databases", English, "Databases", false),
        Doc("hosting", Norwegian, "Mitt web hotell", false),
        Doc("hosting", English, "My web host", false),
        Doc("responsive", Norwegian, "Responsivt design"),
        Doc("responsive", English, "Responsive design")
   )

    val collatz = arrayOf(
        Doc("collatz", Norwegian,"Collatz") ,
        Doc("collatz", English,"Collatz"),
    )

    // Find the first Doc index that matches language code and eventually nonnull segment
    fun getDocIndex(docs: Array<Doc>, languageCode: String, segment: String? = null): Int {
        val usedCode = usedLanguageCode(languageCode)
        val docIndex =  docs.indexOfFirst { (it.language.code == usedCode) && (segment == it.segment || segment == null) }
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

