package com.techreier.edrops.data.blogs.energy

import com.techreier.edrops.data.blogs.BlogPosts
import com.techreier.edrops.domain.Blog
import com.techreier.edrops.domain.BlogPost
import com.techreier.edrops.util.timestamp

object ThermalPower: BlogPosts {
    val timestamp = timestamp("12.05.2025 16:00:00")
    const val SEGMENT = "thermalpower"
    const val TITLE_NO = "Termisk energi"
    const val TITLE_EN = "Thermal power"

    const val SUMMARY_NO = "Termisk energi er varme som er lagret i grunnen, og som kan brukes til oppvarming og i noen tilfeller til produksjon av elektrisitet. " +
            "I Norge brukes termisk energi hovedsakelig til oppvarming gjennom varmepumper og vannbårne systemer. " +
            "I land som Island, hvor det er høy vulkansk aktivitet, brukes geotermisk energi i større grad til å produsere strøm. " +
            "Med teknologiske fremskritt har geotermisk energi et stort potensial som en ren, fornybar energikilde, " +
            "selv om anlegg av og til kan påvirke grunnforhold og nærliggende bygninger."

    const val SUMMARY_EN = "Thermal energy refers to heat stored in the ground, which can be used for heating and, in some regions, for electricity generation. " +
            "In Norway, thermal energy is mainly used for heating through heat pumps and water-based systems. " +
            "In countries like Iceland, where volcanic activity is high, geothermal energy is more widely used to generate electricity. " +
            "With ongoing technological advancements, geothermal energy has significant potential as a clean, " +
            "renewable energy source, although installations can sometimes affect ground conditions and nearby buildings."



    override fun no(blog: Blog): BlogPost = BlogPost(timestamp, SEGMENT, TITLE_NO, SUMMARY_NO, blog)

    override fun en(blog: Blog): BlogPost = BlogPost(timestamp, SEGMENT, TITLE_EN, SUMMARY_EN, blog)
}
