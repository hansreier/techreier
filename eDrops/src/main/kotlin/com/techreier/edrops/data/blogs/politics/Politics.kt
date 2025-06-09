package com.techreier.edrops.data.blogs.politics

import com.techreier.edrops.data.blogs.Blogs
import com.techreier.edrops.data.blogs.addPosts
import com.techreier.edrops.domain.Blog
import com.techreier.edrops.domain.BlogOwner
import com.techreier.edrops.domain.Topic
import com.techreier.edrops.util.timestamp

object Politics : Blogs {
    val timestamp = timestamp("02.05.2025 15:56:00")
    const val SEGMENT = "politics"
    const val SUBJECT_NO = "Politikk"
    const val SUBJECT_EN = "Politics"
    const val POS = 3

    const val ABOUT_NO = "Denne bloggen inneholder korte innlegg om politiske temaer, definisjoner, " +
            "min tolkning og mine eksempler"

    const val ABOUT_EN = "This blog contains short tweets about political themes, definitions, " +
            "my interpretation and my examples"

    override fun no(blogOwner: BlogOwner, topic: Topic) =
        Blog(timestamp, SEGMENT, topic, POS, SUBJECT_NO, ABOUT_NO, mutableListOf(), blogOwner)
            .addPosts(Democracy::no, SymbolPolitics::no, EU::no)

    override fun en(blogOwner: BlogOwner, topic: Topic) =
        Blog(timestamp, SEGMENT, topic, POS, SUBJECT_EN, ABOUT_EN, mutableListOf(), blogOwner)
            .addPosts(Democracy::en, SymbolPolitics::en, EU::en)
}