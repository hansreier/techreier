package com.techreier.edrops.domain

const val MARKDOWN_EXT = ".md"

/**
 * No database needed for rendering docs saved as markdown files.
 * Documents read directly from disk (including definition for drop down menu)
 * Tag is required for file name
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

    fun getDoc(blogId: Long): Doc {
        return doc[blogId.toInt()]
    }

    fun getDocs(langCode: String): List<Doc> {
        return doc.filter { (it.language.code == langCode)  }
    }
}

