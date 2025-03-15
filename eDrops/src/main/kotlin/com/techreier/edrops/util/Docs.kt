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
            MenuItem("home", c.defaultNo, "Hjem"),
            MenuItem("home", c.defaultEn, "Home")
    )
    val views =
        arrayOf(
            MenuItem("ai2084", c.defaultNo, "AI paranoia", false),
            MenuItem("homeoffice", c.defaultNo, "Om hjemmekontor"),
            MenuItem("homeoffice", c.defaultEn, "About home office"),
            MenuItem("energy", c.energyNo, "Energi i Norge"),
            MenuItem("energy", c.energyEn, "Energy in Norway"),
            MenuItem("elpower", c.energyNo, "Elkraft i Norge"),
            MenuItem("elpower", c.energyEn, "Electrical power in Norway"),
            MenuItem("manifest", c.energyNo, "Strøm manifest"),
            MenuItem("manifest", c.energyEn, "Electrical power manifest"),
            MenuItem("elprice", c.energyNo, "Strøm(pris)krisen", false),
            MenuItem("elcrazy", c.energyNo, "To år med elgalskap", false),
            MenuItem("ringsaker", c.energyNo, "Kraft og hytter i Ringsaker", false),
            MenuItem("windpower", c.energyNo, "Myter om vindkraft", false),
            MenuItem("energylinks", c.energyNo, "Energi linker"),
            MenuItem("energylinks", c.energyEn, "Energy links")
        )

    val about =
        arrayOf(
            MenuItem("reier", c.defaultNo, "Meg"),
            MenuItem("reier", c.defaultEn, "Me"),
            MenuItem("links", c.defaultNo, "Mine linker"),
            MenuItem("links", c.defaultEn, "My links"),
            MenuItem("hosting", c.codingNo, "Mitt web hotell", false),
            MenuItem("hosting", c.codingEn, "My web host", false),
            MenuItem("website", c.codingNo, "Nettsted"),
            MenuItem("website", c.codingEn, "Website"),
            MenuItem("readme", c.codingNo, "Prosjektet", false),
            MenuItem("readme", c.codingEn, "Project", false),
            MenuItem("tech", c.codingNo, "Teknologi", false),
            MenuItem("tech", c.codingEn, "Technology", false),
            MenuItem("maintainable", c.codingNo, "Vedlikeholdbarhet"),
            MenuItem("maintainable", c.codingEn, "Maintainability"),
            MenuItem("greenit", c.codingNo, "Grønn IKT"),
            MenuItem("greenit", c.codingEn, "Green ICT"),
            MenuItem("greencode", c.codingNo, "Grønn koding"),
            MenuItem("greencode", c.codingEn, "Green coding"),
            MenuItem("markdown", c.codingNo, "Markdown", false),
            MenuItem("markdown", c.codingEn, "Markdown", false),
            MenuItem("databases", c.codingNo, "Databaser", false),
            MenuItem("databases", c.codingEn, "Databases", false),
            MenuItem("responsive", c.codingNo, "Responsivt design"),
            MenuItem("responsive", c.codingEn, "Responsive design")
        )

    val collatz =
        arrayOf(
            MenuItem("collatz", c.defaultNo, "Collatz"),
            MenuItem("collatz", c.defaultEn, "Collatz"),
        )

    // Find the first Doc index that matches language code and eventually nonnull segment
    // If the languageCode is changed, it also checks against the old language code
    // Note: No check if language codes are valid in project context, because done earlier.
    fun getDocIndex(
        menuItems: Array<MenuItem>,
        oldLangCode: String?,
        usedLangCode: String,
        segment: String? = null,
    ): DocIndex {
        var error = false

        var docIndex =
            menuItems.indexOfFirst { (it.langCode == usedLangCode) && (segment == it.segment || segment == null) }

        if (docIndex < 0) {
            error = true
            if ((oldLangCode != null) && (oldLangCode != usedLangCode)) {
                docIndex = menuItems.indexOfFirst {
                    (it.langCode == oldLangCode)
                            && (segment == it.segment || segment == null)
                }
            }
        }
        return DocIndex(docIndex, error,  menuItems[docIndex].multilingual )
    }

    data class DocIndex(val index: Int, val error: Boolean, val multilingual: Boolean)
}
