package com.techreier.edrops.util

import com.techreier.edrops.domain.Common
import com.techreier.edrops.dto.MenuItem

const val MARKDOWN_EXT = ".md"

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
            Doc("home", c.defaultNo, "Hjem"),
            Doc("home",c.defaultEn,"Home")
    )
    val views =
        arrayOf(
            Doc("ai2084", c.defaultNo, "AI paranoia", false),
            Doc("homeoffice", c.defaultNo, "Om hjemmekontor"),
            Doc("homeoffice", c.defaultEn, "About home office"),
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
            Doc("energylinks", c.energyEn, "Energy links")
        )

    val about =
        arrayOf(
            Doc("reier", c.defaultNo, "Meg"),
            Doc("reier", c.defaultEn, "Me"),
            Doc("links", c.defaultNo, "Mine linker"),
            Doc("links", c.defaultEn, "My links"),
            Doc("hosting", c.defaultNo, "Mitt web hotell", false),
            Doc("hosting", c.defaultEn, "My web host", false),
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
            Doc("responsive", c.codingNo, "Responsivt design"),
            Doc("responsive", c.codingEn, "Responsive design")
        )

    val collatz =
        arrayOf(
            Doc("collatz", c.defaultNo, "Collatz"),
            Doc("collatz", c.defaultEn, "Collatz"),
        )

    // Find the first Doc index that matches language code and eventually nonnull segment
    // If the languageCode is changed, it also checks against the old language code
    // Note: No check if language codes are valid in project context, because done earlier.
    fun getDocIndex(
        docs: Array<Doc>,
        oldLangCode: String?,
        usedLangCode: String,
        segment: String? = null,
    ): DocIndex {
        var error = false

        var docIndex =
            docs.indexOfFirst { (it.topic.language.code == usedLangCode) && (segment == it.segment || segment == null) }

        if (docIndex < 0) {
            error = true
            if ((oldLangCode != null) && (oldLangCode != usedLangCode)) {
                docIndex = docs.indexOfFirst {
                    (it.topic.language.code == oldLangCode)
                            && (segment == it.segment || segment == null)
                }
            }
        }
        return DocIndex(docIndex, error)
    }

    data class DocIndex(val index: Int, val error: Boolean)
}
