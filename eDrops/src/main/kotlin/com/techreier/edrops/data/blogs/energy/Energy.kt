package com.techreier.edrops.data.blogs.energy

import com.techreier.edrops.data.blogs.Blogs
import com.techreier.edrops.data.blogs.addPosts
import com.techreier.edrops.domain.Blog
import com.techreier.edrops.domain.BlogOwner
import com.techreier.edrops.domain.Topic
import com.techreier.edrops.util.timestamp

object Energy : Blogs {
    val timestamp = timestamp("17.05.2025 07:00:00")
    const val SEGMENT = "energy"
    const val SUBJECT_NO = "Energi"
    const val SUBJECT_EN = "Energy"
    const val POS = 3

    const val ABOUT_NB = "Denne bloggen inneholder kort informasjon om vanlige kilder til elektrisitetsproduksjon. " +
            "Innholdet er min tolkning og eksempler. Direkte bruk av energi utenfor strømnettet er ikke i fokus her."

    const val ABOUT_EN = "This blog contains brief information about common energy sources for electricity production. " +
            "The content reflects my interpretation and examples. Direct use of energy outside the power grid is not the focus here."

    override fun no(blogOwner: BlogOwner, topic: Topic) =
        Blog(timestamp, SEGMENT, topic, POS, SUBJECT_NO, ABOUT_NB, mutableListOf(), blogOwner)
            .addPosts(Windpower::no, Solarpower::no, Hydropower::no, Nuclearpower::no,
                Coalpower::no, Gaspower::no, Dieselpower::no, Wavetidalpower::no)

    override fun en(blogOwner: BlogOwner, topic: Topic) =
        Blog(timestamp, SEGMENT, topic, POS, SUBJECT_EN, ABOUT_EN, mutableListOf(), blogOwner)
            .addPosts(Windpower::en, Solarpower::en, Hydropower::en, Nuclearpower::en,
                Coalpower::en, Gaspower::en, Dieselpower::en, Wavetidalpower::en)
}