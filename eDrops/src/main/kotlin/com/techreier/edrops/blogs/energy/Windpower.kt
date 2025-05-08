package com.techreier.edrops.blogs.energy

import com.techreier.edrops.blogs.BlogPosts
import com.techreier.edrops.domain.Blog
import com.techreier.edrops.domain.BlogPost
import com.techreier.edrops.util.timestamp

object Windpower: BlogPosts {
    val timestamp = timestamp("08.05.2025 16:00:00")
    const val SEGMENT = "windpower"
    const val TITLE_NO = "Vindkraft"
    const val TITLE_EN = "Windpower"

    const val SUMMARY_NO = "Vindkraft er en fornybar, men ustabil energikilde som ikke kan styres etter behov. " +
            "Den bygges ofte i naturområder med mye vind. Overproduksjon gir lav pris eller nedstenging, " +
            "mens vindstille gir null produksjon. Derfor trengs alltid stabil balansekraft. " +
            "Landvind er billig, men integrasjonskostnader er høye. Havvind er uansett dyrt. " +
            "De største naturinngrepene er tilførselsveier og kraftlinjer, i tillegg til støy og ressursbruk ved utbygging. " +
            "CO₂-utslipp kan være betydelige ved inngrep i karbonrike områder som myr."


    const val SUMMARY_EN ="Democracy is a model of governance in which the population has meaningful influence where the majority rules, " +
            " either directly or indirectly. It is not a democracy when political decisions are implemented without genuine public involvement."

    override fun no(blog: Blog): BlogPost = BlogPost(timestamp, SEGMENT, TITLE_NO, SUMMARY_NO, blog)

    override fun en(blog: Blog): BlogPost = BlogPost(timestamp, SEGMENT, TITLE_EN, SUMMARY_EN, blog)
}
