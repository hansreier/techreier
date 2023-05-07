package com.sigmondsmart.edrops.domain

object Docs {

    val doc = listOf(
        Doc("goals", "goalsN.md", Norwegian, "Mål"),
        Doc("goals", "goals.md", English, "Goals"),
        Doc("readme", "readme.md", Norwegian, "Om prosjektet"),
        Doc("readme", "readme.md", English, "Read me"),
    )

    fun getDoc(blogId: Long): Doc {
        return doc[blogId.toInt()]
    }

    fun getDocs(langCode: String): List<Doc> {
        return doc.filter { it.language.code == langCode }
    }
}

