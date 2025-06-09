package com.techreier.edrops.data.blogs.climatenv

import com.techreier.edrops.data.blogs.Blogs
import com.techreier.edrops.data.blogs.addPosts
import com.techreier.edrops.domain.Blog
import com.techreier.edrops.domain.BlogOwner
import com.techreier.edrops.domain.Topic
import com.techreier.edrops.util.timestamp

object Climatenv : Blogs {
    val timestamp = timestamp("17.05.2025 07:00:00")
    const val SEGMENT = "climatenv"
    const val SUBJECT_NO = "Klima og miljø"
    const val SUBJECT_EN = "Climate and enviroment"
    const val POS = 2

    const val ABOUT_NB =
        "Denne bloggen relaterer klima til natur og miljø. " +
                "Det viktigste er at alt henger sammen med alt. " +
                "Klimaproblemet løses ikke isolert fra miljøproblemer og tap av natur. " +
                "Naturen består av ulike økosystemer, som er et komplekst samspill mellom planter, " +
                "dyr, landskap, vann og klima. " +
                "Over tid vil dette samspillet endre seg naturlig, " +
                "men jeg er redd det skjer altfor fort på grunn av menneskelig påvirkning."

    const val ABOUT_EN =
        "This blog connects climate with nature and the environment. " +
                "The most important thing is that everything is connected to everything else. " +
                "The climate crisis cannot be solved in isolation from environmental problems and the loss of nature. " +
                "Nature consists of various ecosystems, which are complex interactions between plants, " +
                "animals, landscapes, water, and climate. " +
                "Throughout history, climate has reshaped these interactions many times — just think of the age of the dinosaurs."

    override fun no(blogOwner: BlogOwner, topic: Topic) =
        Blog(timestamp, SEGMENT, topic, POS, SUBJECT_NO, ABOUT_NB, mutableListOf(), blogOwner)
            .addPosts(Green::no, Sustainability::no, SustainabilityReporting::no, Quicker::no)

    override fun en(blogOwner: BlogOwner, topic: Topic) =
        Blog(timestamp, SEGMENT, topic, POS, SUBJECT_EN, ABOUT_EN, mutableListOf(), blogOwner)
            .addPosts(Green::en, Sustainability::en, SustainabilityReporting::en, Quicker::en)
}