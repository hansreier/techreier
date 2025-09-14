package com.techreier.edrops.data

import com.techreier.edrops.dto.MenuItem

const val MARKDOWN_EXT = ".md"

/**
 * No database needed for rendering docs saved as markdown files.
 * Documents read directly from disk.
 * Documents are fetch from drop down menu
 * The segment is the file name without extension
 */
object Docs {
    val c =
        Base()
    val home =
        arrayOf(
            MenuItem("home", c.defaultNo, "Hjem"),
            MenuItem("home", c.defaultEn, "Home")
    )
    val views =
        arrayOf(
            MenuItem("ai2084", c.defaultNo, "AI paranoia"),
            MenuItem("homeoffice", c.defaultNo, "Om hjemmekontor"),
            MenuItem("homeoffice", c.defaultEn, "About home office"),
            MenuItem("tax", c.defaultNo,"Om skatt"),
            MenuItem("energy", c.energyNo, "Energi i Norge"),
            MenuItem("energy", c.energyEn, "Energy in Norway"),
            MenuItem("energypath", c.energyNo, "Energi veikart"),
            MenuItem("energypath", c.energyEn, "Energy roadmap"),
            MenuItem("elpower", c.energyNo, "Elkraft i Norge"),
            MenuItem("elpower", c.energyEn, "Electrical power in Norway"),
            MenuItem("manifest", c.energyNo, "Strøm manifest"),
            MenuItem("manifest", c.energyEn, "Electrical power manifest"),
            MenuItem("elprice", c.energyNo, "Strøm(pris)krisen"),
            MenuItem("elcrazy", c.energyNo, "To år med elgalskap"),
            MenuItem("ringsaker", c.energyNo, "Kraft og hytter i Ringsaker"),
            MenuItem("windpower", c.energyNo, "Myter om vindkraft"),
            MenuItem("elprod", c.energyNo, "Om elproduksjon og forbruk" ),
            MenuItem("energylinks", c.energyNo, "Energi linker"),
            MenuItem("energylinks", c.energyEn, "Energy links")
        )

    val about =
        arrayOf(
            MenuItem("reier", c.defaultNo, "Meg"),
            MenuItem("reier", c.defaultEn, "Me"),
            MenuItem("links", c.defaultNo, "Mine linker"),
            MenuItem("links", c.defaultEn, "My links"),
            MenuItem("website", c.defaultNo, "Nettsted"),
            MenuItem("website", c.defaultEn, "Website"),
            MenuItem("coderwanted", c.defaultNo, "Programmerer ønskes"),
            MenuItem("coderwanted", c.defaultEn, "Programmer wanted"),
            MenuItem("hosting", c.codingNo, "Mitt web hotell"),
            MenuItem("hosting", c.codingEn, "My web host"),
            MenuItem("readme", c.codingNo, "Prosjektet"),
            MenuItem("readme", c.codingEn, "Project"),
            MenuItem("tech", c.codingNo, "Teknologi"),
            MenuItem("tech", c.codingEn, "Technology"),
            MenuItem("maintainable", c.codingNo, "Vedlikeholdbarhet"),
            MenuItem("maintainable", c.codingEn, "Maintainability"),
            MenuItem("greenit", c.codingNo, "Grønn IKT"),
            MenuItem("greenit", c.codingEn, "Green ICT"),
            MenuItem("greencode", c.codingNo, "Grønn koding"),
            MenuItem("greencode", c.codingEn, "Green coding"),
            MenuItem("markdown", c.codingNo, "Markdown"),
            MenuItem("markdown", c.codingEn, "Markdown"),
            MenuItem("databases", c.codingNo, "Databaser"),
            MenuItem("databases", c.codingEn, "Databases"),
            MenuItem("responsive", c.codingNo, "Responsivt design"),
            MenuItem("responsive", c.codingEn, "Responsive design"),
            MenuItem("gui", c.codingNo, "GUI design og implementasjon"),
            MenuItem("gui", c.codingEn, "GUI design and implementation"),
            MenuItem("zerotrust", c.codingNo, "Zero Trust"),
            MenuItem("zerotrust", c.codingEn, "Zero Trust"),

        )

    val collatz =
        arrayOf(
            MenuItem("collatz", c.defaultNo, "Collatz"),
            MenuItem("collatz", c.defaultEn, "Collatz")
        )

    val fraction =
        arrayOf(
            MenuItem("fraction", c.defaultNo, "Beste brøk"),
            MenuItem("fraction", c.defaultEn, "Best fraction")
        )

    val energyProd =
        arrayOf(
            MenuItem("energyprod", c.defaultNo, "Energiproduksjon Norge"),
            MenuItem("energyprod", c.defaultEn, "Energy prodution Norway")
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
        return DocIndex(docIndex, error )
    }

    data class DocIndex(val index: Int, val error: Boolean)
}
