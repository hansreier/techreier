package com.techreier.edrops.util

import com.techreier.edrops.domain.English
import com.techreier.edrops.domain.Norwegian
import com.techreier.edrops.domain.Topic
import com.techreier.edrops.domain.Topic.Companion.CODING
import com.techreier.edrops.domain.Topic.Companion.DEFAULT
import com.techreier.edrops.domain.Topic.Companion.ENERGY
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
    val defaultNo = Topic(DEFAULT, Norwegian)
    val defaultEn = Topic(DEFAULT, English)
    val energyNo = Topic(ENERGY, Norwegian)
    val energyEn = Topic(ENERGY, English)
    val codingNo = Topic(CODING, Norwegian)
    val codingEn = Topic(CODING, English)
    val home =
        arrayOf(
            Doc("energy", energyNo, "Energi i Norge"),
            Doc("energy", energyEn, "Energy in Norway"),
            Doc("elpower", energyNo, "Elkraft i Norge"),
            Doc("elpower", energyEn, "Electrical power in Norway"),
            Doc("manifest", energyNo, "Strøm manifest"),
            Doc("manifest", energyEn, "Electrical power manifest"),
            Doc("elprice", energyNo, "Strøm(pris)krisen", false),
            Doc("elcrazy", energyNo, "To år med elgalskap", false),
            Doc("ringsaker", energyNo, "Kraft og hytter i Ringsaker", false),
            Doc("windpower", energyNo, "Myter om vindkraft", false),
            Doc("energylinks", energyNo, "Energi linker"),
            Doc("energylinks", energyEn, "Energy links"),
            Doc("ai2084", energyNo, "AI paranoia", false),
            Doc("homeoffice", defaultNo, "Om hjemmekontor"),
            Doc("homeoffice", defaultEn, "About home office"),
        )

    val about =
        arrayOf(
            Doc("reier", defaultNo, "Meg"),
            Doc("reier", defaultEn, "Me"),
            Doc("links", defaultNo, "Mine linker"),
            Doc("links", defaultEn, "My links"),
            Doc("website", codingNo, "Nettsted"),
            Doc("website", codingEn, "Website"),
            Doc("readme", codingNo, "Prosjektet", false),
            Doc("readme", codingEn, "Project", false),
            Doc("tech", codingNo, "Teknologi", false),
            Doc("tech", codingEn, "Technology", false),
            Doc("maintainable", codingNo, "Vedlikeholdbarhet"),
            Doc("maintainable", codingEn, "Maintainability"),
            Doc("greenit", codingNo, "Grønn IKT"),
            Doc("greenit", codingEn, "Green ICT"),
            Doc("greencode", codingNo, "Grønn koding"),
            Doc("greencode", codingEn, "Green coding"),
            Doc("markdown", codingNo, "Markdown", false),
            Doc("markdown", codingEn, "Markdown", false),
            Doc("databases", codingNo, "Databaser", false),
            Doc("databases", codingEn, "Databases", false),
            Doc("hosting", defaultNo, "Mitt web hotell", false),
            Doc("hosting", defaultEn, "My web host", false),
            Doc("responsive", codingNo, "Responsivt design"),
            Doc("responsive", codingEn, "Responsive design"),
        )

    val collatz =
        arrayOf(
            Doc("collatz", defaultNo, "Collatz"),
            Doc("collatz", defaultEn, "Collatz"),
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
