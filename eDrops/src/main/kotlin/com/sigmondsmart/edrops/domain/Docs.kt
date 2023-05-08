package com.sigmondsmart.edrops.domain

const val DEFAULT_MARKDOWN_DIR = "src/main/resources/markdown"
const val MARKDOWN_EXT = ".md"

object Docs {
    val doc = listOf(
        Doc("goals",  Norwegian, "Mål"),
        Doc("goals",  English, "Goals"),
        Doc("readme", Norwegian, "Om prosjektet", false),
        Doc("readme", English, "Read me", false)
    )

    fun getDoc(blogId: Long): Doc {
        return doc[blogId.toInt()]
    }

    fun getDocs(langCode: String): List<Doc> {
        return doc.filter { (it.language.code == langCode)  }
    }
}

