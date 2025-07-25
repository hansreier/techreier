package com.techreier.edrops.data.blogs.energy

import com.techreier.edrops.data.blogs.Blogs
import com.techreier.edrops.data.blogs.addPosts
import com.techreier.edrops.domain.Blog
import com.techreier.edrops.domain.BlogOwner
import com.techreier.edrops.domain.Topic
import com.techreier.edrops.util.timestamp

object Elpower : Blogs {
    val timestamp = timestamp("17.05.2025 07:00:00")
    const val SEGMENT = "elpower"
    const val SUBJECT_NO = "Elkraft"
    const val SUBJECT_EN = "Electrical power"
    const val POS = 3

    const val ABOUT_NO = "Denne bloggen inneholder kort informasjon om vanlige kilder til elektrisitetsproduksjon. " +
            "Innholdet er min tolkning og eksempler. Direkte bruk av energi utenfor strømnettet er ikke i fokus her."

    const val ABOUT_EN =
        "This blog contains brief information about common energy sources for electricity production. " +
                "The content reflects my interpretation and examples. Direct use of energy outside the power grid is not the focus here."

    override fun no(blogOwner: BlogOwner, topic: Topic) =
        Blog(timestamp, SEGMENT, topic, POS, SUBJECT_NO, ABOUT_NO, mutableListOf(), blogOwner)
            .addPosts(
                Twh::no, NorwegianEnergyLaw::no, Windpower::no, Solarpower::no, Hydropower::no, Nuclearpower::no,
                Coalpower::no, Gaspower::no, Dieselpower::no, Wavetidalpower::no, ThermalPower::no
            )

    override fun en(blogOwner: BlogOwner, topic: Topic) =
        Blog(timestamp, SEGMENT, topic, POS, SUBJECT_EN, ABOUT_EN, mutableListOf(), blogOwner)
            .addPosts(
                Twh::en, NorwegianEnergyLaw::en, Windpower::en, Solarpower::en, Hydropower::en, Nuclearpower::en,
                Coalpower::en, Gaspower::en, Dieselpower::en, Wavetidalpower::en, ThermalPower::en
            )
}