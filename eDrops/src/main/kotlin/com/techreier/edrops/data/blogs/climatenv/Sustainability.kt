package com.techreier.edrops.data.blogs.climatenv

import com.techreier.edrops.data.blogs.BlogPosts
import com.techreier.edrops.domain.Blog
import com.techreier.edrops.domain.BlogPost
import com.techreier.edrops.util.timestamp

object Sustainability : BlogPosts {
    val timestamp = timestamp("12.05.2025 16:00:00")
    const val SEGMENT = "sustainable"
    const val TITLE_NO = "Bærekraft"
    const val TITLE_EN = "SustainabilityReporting"

    const val SUMMARY_NO = """
Bærekraft handler om å dekke dagens menneskelige behov uten å undergrave naturens tålegrenser eller 
mulighetene for framtidige generasjoner. 
FN’s 17 bærekraftsmål kan deles inn i fem hovedkategorier: 
- Mennesker (fattigdom, helse, utdanning), 
- Miljø (natur, klima, ressurser), 
- Velstand (energi, arbeidsliv, økonomi), 
- Fred og rettferdighet
- Internasjonalt samarbeid.  

Denne typen gruppering gjør det lettere å se helheten – og konfliktene – mellom målene.
"""

    const val SUMMARY_EN = """
Sustainability is about meeting the needs of today's people without undermining the boundaries of nature or 
the opportunities for future generations. 
The UN’s 17 Sustainable Development Goals can be divided into five main categories: 
- People (poverty, health, education), 
- Environment (nature, climate, resources), 
- Prosperity (energy, work, economy), 
- Peace and justice, 
- International cooperation.  

This type of grouping makes it easier to see the bigger picture – and the conflicts – between the goals.
"""

    override fun no(blog: Blog): BlogPost = BlogPost(timestamp, SEGMENT, TITLE_NO, SUMMARY_NO, blog)

    override fun en(blog: Blog): BlogPost = BlogPost(timestamp, SEGMENT, TITLE_EN, SUMMARY_EN, blog)
}
