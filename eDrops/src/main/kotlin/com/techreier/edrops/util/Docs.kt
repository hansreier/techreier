package com.techreier.edrops.util

import com.techreier.edrops.domain.Common
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
    val c =
        Common()
    val home =
        arrayOf(
            Doc("energy", c.energyNo, "Energi i Norge"),
            Doc("energy", c.energyEn, "Energy in Norway"),
            Doc("elpower", c.energyNo, "Elkraft i Norge"),
            Doc("elpower", c.energyEn, "Electrical power in Norway"),
            Doc("manifest", c.energyNo, "Strøm manifest"),
            Doc("manifest", c.energyEn, "Electrical power manifest"),
            Doc("elprice", c.energyNo, "Strøm(pris)krisen", false),
            Doc("elcrazy", c.energyNo, "To år med elgalskap", false),
            Doc("ringsaker", c.energyNo, "Kraft og hytter i Ringsaker", false),
            Doc("windpower", c.energyNo, "Myter om vindkraft", false),
            Doc("energylinks", c.energyNo, "Energi linker"),
            Doc("energylinks", c.energyEn, "Energy links"),
            Doc("ai2084", c.energyNo, "AI paranoia", false),
            Doc("homeoffice", c.defaultNo, "Om hjemmekontor"),
            Doc("homeoffice", c.defaultEn, "About home office"),
        )

    val about =
        arrayOf(
            Doc("reier", c.defaultNo, "Meg"),
            Doc("reier", c.defaultEn, "Me"),
            Doc("links", c.defaultNo, "Mine linker"),
            Doc("links", c.defaultEn, "My links"),
            Doc("website", c.codingNo, "Nettsted"),
            Doc("website", c.codingEn, "Website"),
            Doc("readme", c.codingNo, "Prosjektet", false),
            Doc("readme", c.codingEn, "Project", false),
            Doc("tech", c.codingNo, "Teknologi", false),
            Doc("tech", c.codingEn, "Technology", false),
            Doc("maintainable", c.codingNo, "Vedlikeholdbarhet"),
            Doc("maintainable", c.codingEn, "Maintainability"),
            Doc("greenit", c.codingNo, "Grønn IKT"),
            Doc("greenit", c.codingEn, "Green ICT"),
            Doc("greencode", c.codingNo, "Grønn koding"),
            Doc("greencode", c.codingEn, "Green coding"),
            Doc("markdown", c.codingNo, "Markdown", false),
            Doc("markdown", c.codingEn, "Markdown", false),
            Doc("databases", c.codingNo, "Databaser", false),
            Doc("databases", c.codingEn, "Databases", false),
            Doc("hosting", c.defaultNo, "Mitt web hotell", false),
            Doc("hosting", c.defaultEn, "My web host", false),
            Doc("responsive", c.codingNo, "Responsivt design"),
            Doc("responsive", c.codingEn, "Responsive design"),
        )

    val collatz =
        arrayOf(
            Doc("collatz", c.defaultNo, "Collatz"),
            Doc("collatz", c.defaultEn, "Collatz"),
        )

    // Find the first Doc index that matches language code and eventually nonnull segment
    fun getDocIndex(
        docs: Array<Doc>,
        languageCode: String,
        segment: String? = null,
    ): Int {
        val usedCode = usedLanguageCode(languageCode)
        val docIndex =
            docs.indexOfFirst { (it.topic.language.code == usedCode) && (segment == it.segment || segment == null) }
        if (docIndex < 0) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, ILLEGAL_PATH)
        }
        return docIndex
    }

    fun getDocs(
        docs: Array<Doc>,
        languageCode: String,
    ): List<Doc> {
        val usedCode = usedLanguageCode(languageCode)
        return docs.filter { (it.topic.language.code == usedCode) }
    }

    // Rule for returning language code used in this project from unknown codes.
    fun usedLanguageCode(languageCode: String): String = if (languageCode in listOf("nn", "no", "nb")) "nb" else "en"
}
