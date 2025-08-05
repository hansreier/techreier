package com.techreier.edrops.data.blogs.politics

import com.techreier.edrops.data.blogs.BlogPosts
import com.techreier.edrops.domain.Blog
import com.techreier.edrops.domain.BlogPost
import com.techreier.edrops.util.timestamp

object EnergyPolitics: BlogPosts {
    val timestamp = timestamp("30.04.2025 16:56:00")
    const val SEGMENT = "energyPoliics"
    const val TITLE_NO = "Energi politikk"
    const val TITLE_EN = "Energy politics"

    const val SUMMARY_NO = """
Rask utfasing av olje- og gass-produksjon i Norge for å tilfredsstille klimamål 
vil ikke fungere og virker mot sin hensikt. 
Ideen om at Norge er Europas batteri må legges død. 
Fornybar elkraft vil aldri fullt ut erstatte fossil energiproduksjon.
Uregulerbar vind- og solkraft som bygger ned natur og øker nettleia må unngås. 
Kjernekraft er nødvendig internasjonalt, og bør vurderes også i Norge. 
Påstander om et kommende kraftunderskudd i Norge er sterkt overdrevet.
"""

    const val SUMMARY_EN = """
A rapid phase-out of oil and gas production in Norway to meet climate goals
will not work and is counterproductive.
The idea that Norway is Europe’s battery must be abandoned.
Renewable electricity will never fully replace fossil energy production.
Unregulated wind and solar power that degrades nature and increases grid fees must be avoided.
Nuclear power is necessary internationally and should also be considered in Norway.
Claims of an impending power shortage in Norway are greatly exaggerated.
"""

    override fun no(blog: Blog): BlogPost = BlogPost(timestamp, SEGMENT, TITLE_NO, SUMMARY_NO, blog)

    override fun en(blog: Blog): BlogPost = BlogPost(timestamp, SEGMENT, TITLE_EN, SUMMARY_EN, blog)

}