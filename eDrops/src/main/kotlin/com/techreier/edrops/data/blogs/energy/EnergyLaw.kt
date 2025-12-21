package com.techreier.edrops.data.blogs.energy

import com.techreier.edrops.data.blogs.BlogPosts
import com.techreier.edrops.domain.Blog
import com.techreier.edrops.domain.BlogPost
import com.techreier.edrops.util.strip
import com.techreier.edrops.util.timestamp

object EnergyLaw : BlogPosts {
    val timestamp = timestamp("25.07.2025 12:00:00")
    const val SEGMENT = "energylaw"
    const val TITLE_NO = "Energiloven (1990)"
    const val TITLE_EN = "Norwegian energy law (1990)"

    const val SUMMARY_NO = """
**§ 1-2. (Formål)**
Loven skal sikre at produksjon, omforming, overføring, omsetning, fordeling og bruk av energi
foregår på en samfunnsmessig rasjonell måte,
herunder skal det tas hensyn til allmenne og private interesser som blir berørt.

Men sikrer virkelig loven dette? Eksport kan ikke bremses før situasjonen er kritisk.
Strømprisene er blitt høye og ustabile, drevet av variabel vind- og solkraft og sterkere markedskopling til Europa.
Store prisforskjeller mellom områder er konkurransevridende og svekker tilliten.
"""

    const val SUMMARY_EN = """
**§ 1-2. (Purpose)**
The law shall ensure that the production, conversion, transmission, trading, distribution, and use of energy 
take place in a socially rational manner, 
and that both public and private interests affected are taken into account.

But does the law truly ensure this? Export from Norway cannot be limited until the situation is critical.  
Electricity prices have become high and unstable, driven by variable wind and solar power and stronger market integration with Europe.
Large price differences between regions distort competition and undermine public trust.
"""

    override fun no(blog: Blog): BlogPost = BlogPost(timestamp, SEGMENT, TITLE_NO, SUMMARY_NO.strip(), blog)

    override fun en(blog: Blog): BlogPost = BlogPost(timestamp, SEGMENT, TITLE_EN, SUMMARY_EN.strip(), blog)
}
