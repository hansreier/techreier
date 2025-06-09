package com.techreier.edrops.data.blogs.coding

import com.techreier.edrops.data.blogs.BlogPosts
import com.techreier.edrops.domain.Blog
import com.techreier.edrops.domain.BlogPost
import com.techreier.edrops.util.timestamp

object Vibecoding : BlogPosts {
    val timestamp = timestamp("12.05.2025 16:00:00")
    const val SEGMENT = "vibecoding"
    const val TITLE_NO = "Vibbe koding"
    const val TITLE_EN = "Vibe coding"

    const val SUMMARY_NO =
        "Vibe-koding er å bruke AI til å lage kode med naturlig språk, så teste eller inspisere resultatet raskt," +
                " og be AI-en forbedre det — om og om igjen — til det blir perfekt." +
                " I teorien erstatter dette programmerere, og hvem som helst kan gjøre det." +
                " Noen AI-verktøy passer bedre til denne stilen, som Cursor" +
                " Ideelt sett tester og fikser AI-en koden helt selv."

    const val SUMMARY_EN =
        "Vibe coding is using AI to produce code with natural language, then quickly test or inspect the result," +
                " and ask the AI to improve it — over and over — until it's perfect." +
                " In theory, this replaces programmers, and anyone can do it." +
                " Some AI tools are better suited to this style, like Cursor" +
                " Ideally, the AI would just test and fix the code by itself."
    override fun no(blog: Blog): BlogPost = BlogPost(timestamp, SEGMENT, TITLE_NO, SUMMARY_NO, blog)

    override fun en(blog: Blog): BlogPost = BlogPost(timestamp, SEGMENT, TITLE_EN, SUMMARY_EN, blog)
}
