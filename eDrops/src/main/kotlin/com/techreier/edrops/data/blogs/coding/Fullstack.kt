package com.techreier.edrops.data.blogs.coding

import com.techreier.edrops.data.blogs.BlogPosts

import com.techreier.edrops.domain.Blog

import com.techreier.edrops.domain.BlogPost
import com.techreier.edrops.util.strip
import com.techreier.edrops.util.timestamp

object Fullstack : BlogPosts {
    val timestamp = timestamp("09.06.2025 21:00:00")
    const val SEGMENT = "fullstack"
    const val TITLE_NO = "Fullstack"
    const val TITLE_EN = "Fullstack"

    const val SUMMARY_NO = "En fullstack-utvikler er en utvikler som tror han kan alt, " +
            "og som er ettertraktet blant arbeidsgivere fordi de tror han kan alt, " +
            "men som i realiteten kan lite om alt. Fullstack eksisterte egentlig ikke " +
            "før web'en splittet all utvikling i en frontend- og en backend-del."

    const val SUMMARY_EN = "A fullstack developer is a developer who thinks he can do everything, " +
            "and is popular among employers because they think he knows everything, " +
            "but in reality he knows little about everything. Fullstack didn't really exist " +
            "before the web split all development into a frontend and a backend part."

    override fun no(blog: Blog): BlogPost = BlogPost(timestamp, SEGMENT, TITLE_NO, SUMMARY_NO.strip(), blog)

    override fun en(blog: Blog): BlogPost = BlogPost(timestamp, SEGMENT, TITLE_EN, SUMMARY_EN.strip(), blog)

}