package com.techreier.edrops.blogs.coding

import com.techreier.edrops.blogs.Blogs
import com.techreier.edrops.blogs.addPosts
import com.techreier.edrops.domain.Blog
import com.techreier.edrops.domain.BlogOwner
import com.techreier.edrops.domain.Topic
import com.techreier.edrops.util.timestamp

object Coding : Blogs {
    val timestamp = timestamp("17.05.2025 07:00:00")
    const val SEGMENT = "coding"
    const val SUBJECT_NO = "Koding"
    const val SUBJECT_EN = "Coding"
    const val POS = 2

    const val ABOUT_NB = "Denne bloggen inneholder korte innlegg om koding – kunsten å få datamaskiner til å gjøre som du vil. " +
            "Et mer formelt ord for koding er programmering. " +
            "Kode er rett og slett instruksjoner datamaskinen forstår. " +
            "Et program er en samling kodefiler som er kjørt gjennom en kompilator, " +
            "og kan enkelt startes med et tastetrykk eller en kommando."


    const val ABOUT_EN = "This blog contains short posts about coding – the art of making computers do what you want. " +
            "A more formal word for coding is programming. " +
            "Code is simply a set of instructions the computer understands. " +
            "A program is a collection of code files run through a compiler" +
            " and easily started with a command or the click of a button."

    override fun no(blogOwner: BlogOwner, topic: Topic) =
        Blog(timestamp, SEGMENT, topic, POS, SUBJECT_NO, ABOUT_NB, mutableListOf(), blogOwner)
            .addPosts(Vibecoding::no)

    override fun en(blogOwner: BlogOwner, topic: Topic) =
        Blog(timestamp, SEGMENT, topic, POS, SUBJECT_EN, ABOUT_EN, mutableListOf(), blogOwner)
            .addPosts(Vibecoding::en)
}