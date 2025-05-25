package com.techreier.edrops.data.blogs.energy

import com.techreier.edrops.data.blogs.BlogPosts
import com.techreier.edrops.domain.Blog
import com.techreier.edrops.domain.BlogPost
import com.techreier.edrops.util.timestamp

object Solarpower: BlogPosts {
    val timestamp = timestamp("08.05.2025 16:00:00")
    const val SEGMENT = "solarpower"
    const val TITLE_NO = "Solkraft"
    const val TITLE_EN = "Solar power"

    const val SUMMARY_NO = "Solkraft er en fornybar, men ustabil energikilde som ikke kan styres etter behov. " +
            "Solceller produserer mest energi om sommeren, når etterspørselen ofte er lav. " +
            "I liten skala er solenergi fornuftig – som i Casio-klokken min, på hytta eller på taket av bygninger. " +
            "I stor skala er det mer problematisk, særlig når det går ut over natur, og det krever store nettinvesteringer. " +
            "Solparkers plassering kan ha betydelig innvirkning på natur og arealbruk. " +
            "Produksjonen av solceller krever energi og naturressurser, noe som svekker klimaeffekten."

    const val SUMMARY_EN ="Solar power is a renewable but unstable energy source that cannot be controlled on demand. " +
            "Solar panels produce the most energy in the summer, when demand is often low. " +
            "On a small scale, solar energy makes sense – like in my Casio watch, at the cabin, or on building rooftops. " +
            "On a large scale, it becomes more problematic with impact on nature and it requires significant grid investments. " +
            "Solar park placement can have a considerable impact on nature and land use. " +
            "The production of solar panels requires energy and natural resources, which diminishes their climate benefit. "

    override fun no(blog: Blog): BlogPost = BlogPost(timestamp, SEGMENT, TITLE_NO, SUMMARY_NO, blog)

    override fun en(blog: Blog): BlogPost = BlogPost(timestamp, SEGMENT, TITLE_EN, SUMMARY_EN, blog)
}
