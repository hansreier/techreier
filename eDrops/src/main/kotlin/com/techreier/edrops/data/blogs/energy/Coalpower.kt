package com.techreier.edrops.data.blogs.energy

import com.techreier.edrops.data.blogs.BlogPosts
import com.techreier.edrops.domain.Blog
import com.techreier.edrops.domain.BlogPost
import com.techreier.edrops.util.timestamp

object Coalpower: BlogPosts {
    val timestamp = timestamp("12.05.2025 16:00:00")
    const val SEGMENT = "coalpower"
    const val TITLE_NO = "Kullkraft"
    const val TITLE_EN = "Coal power"

    const val SUMMARY_NO = "Kullkraft er en fossil energikilde hentet fra gruver under bakken. " +
            "Kull er blant de mest forurensende energikildene på grunn av høye CO2-utslipp og partikkelutslipp. " +
            "Flere deler av verden er fortsatt avhengige av kull til oppvarming og elektrisitet. " +
            "I Norge finnes det kullgruver på Svalbard som nå fases ut. " +
            "Ironisk nok kan kull fortsatt forsvares der på grunn av øyas isolerte beliggenhet og transportutfordringer."

    const val SUMMARY_EN = "Coal power is a fossil energy source extracted from underground mines. " +
            "Coal is among the most polluting energy sources due to high CO2 emissions and particulate pollution. " +
            "Many parts of the world still rely on coal for heating and electricity. " +
            "In Norway, there are coal mines on Svalbard which are now being phased out. " +
            "Ironically, coal may still be justifiable there due to the island's remote location and transport challenges."

    override fun no(blog: Blog): BlogPost = BlogPost(timestamp, SEGMENT, TITLE_NO, SUMMARY_NO, blog)

    override fun en(blog: Blog): BlogPost = BlogPost(timestamp, SEGMENT, TITLE_EN, SUMMARY_EN, blog)
}
