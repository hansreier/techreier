package com.sigmondsmart.edrops.domain

object Docs {
    var count = 0L
    val doc = listOf(
        Doc("goals", "goalsN.md", Norwegian),
        Doc("readme", "readme.md", Norwegian),
        Doc("readme", "readme.md", English),
        Doc("goals", "goals.md", English),
        Doc("not found", "notFound.md", English)
    )

    fun getDoc(blogId: Long): Doc {
        return doc[blogId.toInt()]
    }

    fun getDocs(langCode: String): List<Doc> {
        return doc.filter { it.language.code == langCode }
    }
}

