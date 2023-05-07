package com.sigmondsmart.edrops.domain

object Docs {
    val doc = listOf(
        Doc("goals", "goalsN.md", Norwegian, -1),
        Doc("readme","readme.md", Norwegian, -2),
        Doc("readme", "readme.md", English, -3),
        Doc("goals", "goals.md", English, -4)
    )

    fun getDoc(blogId: Long): Doc {
        return doc[-blogId.toInt() -1]
    }

    fun getDocs(langCode: String): List<Doc> {
        return doc.filter { it.language.code == langCode }
    }
}

