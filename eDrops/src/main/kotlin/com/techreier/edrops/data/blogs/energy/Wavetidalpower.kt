package com.techreier.edrops.data.blogs.energy

import com.techreier.edrops.data.blogs.BlogPosts
import com.techreier.edrops.domain.Blog
import com.techreier.edrops.domain.BlogPost
import com.techreier.edrops.util.strip
import com.techreier.edrops.util.timestamp

object Wavetidalpower: BlogPosts {
    val timestamp = timestamp("18.05.2025 23:15:00")
    const val SEGMENT = "wavetidalpower"
    const val TITLE_NO = "Bølge- & tidevanns-kraft"
    const val TITLE_EN = "Wave & tidal power"

    const val SUMMARY_NO = "Bølgekraft har stort potensial og kan gi bedre stabilitet enn vindkraft, " +
            "særlig fordi bølger ofte varer lenger etter at vinden har løya. " +
            "Det kan også kombineres med havvind. " +
            "Begrenset kommersialisering skyldes mer finansiering og stordriftsproblemer enn teknologi. " +
            "Tidevannskraft er stabil, men geografisk begrenset (MeyGen, Skottland). " +
            "Konsekvenser for fisk er usikre."

    const val SUMMARY_EN = "Wave power has large potential and may offer better stability than wind, " +
            "especially as waves can persist after the wind dies. " +
            "It can also be combined with offshore wind. " +
            "Limited commercialization is mainly due to funding and scaling, not technology. " +
            "Tidal power is stable but geographically limited (MeyGen, Scotland). " +
            "Environmental impact on fish remains uncertain."

    override fun no(blog: Blog): BlogPost = BlogPost(timestamp, SEGMENT, TITLE_NO, SUMMARY_NO.strip(), blog)

    override fun en(blog: Blog): BlogPost = BlogPost(timestamp, SEGMENT, TITLE_EN, SUMMARY_EN.strip(), blog)
}
