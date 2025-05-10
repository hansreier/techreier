package com.techreier.edrops.blogs.energy

import com.techreier.edrops.blogs.BlogPosts
import com.techreier.edrops.domain.Blog
import com.techreier.edrops.domain.BlogPost
import com.techreier.edrops.util.timestamp

object Windpower: BlogPosts {
    val timestamp = timestamp("10.05.2025 16:00:00")
    const val SEGMENT = "windpower"
    const val TITLE_NO = "Vindkraft"
    const val TITLE_EN = "Wind power"

    const val SUMMARY_NO = "Vindkraft er en fornybar, men ustabil energikilde som ikke kan styres etter behov. " +
            "Den bygges ofte i naturområder med mye vind. Overproduksjon gir lav pris eller nedstenging, " +
            "mens vindstille gir null produksjon. Derfor trengs alltid stabil balansekraft. " +
            "Landvind er billig, men integrasjonskostnader er høye. Havvind er uansett dyrt. " +
            "De største naturinngrepene er tilførselsveier og kraftlinjer, i tillegg til støy og ressursbruk ved utbygging. " +
            "CO₂-utslipp kan være betydelige ved inngrep i karbonrike områder som myr."

    const val SUMMARY_EN ="Wind power is a renewable but unstable energy source that cannot be adjusted to demand. " +
            "Turbines are often built in windy natural areas. Excess wind can cause overproduction, " +
            "leading to low prices or shutdowns, while calm weather means no production. " +
            "Stable backup power is always needed. Onshore wind is cheap, but grid integration is costly. " +
            "Offshore wind remains expensive.  " +
            "The largest environmental impacts come from roads and power lines, " +
            "along with noise and resource use during construction. " +
            "CO₂ emissions can be significant when building on carbon-rich areas like peatlands."

    override fun no(blog: Blog): BlogPost = BlogPost(timestamp, SEGMENT, TITLE_NO, SUMMARY_NO, blog)

    override fun en(blog: Blog): BlogPost = BlogPost(timestamp, SEGMENT, TITLE_EN, SUMMARY_EN, blog)
}
