package com.techreier.edrops.data.blogs.climatenv

import com.techreier.edrops.data.blogs.BlogPosts
import com.techreier.edrops.domain.Blog
import com.techreier.edrops.domain.BlogPost
import com.techreier.edrops.util.timestamp

object MoreFaster : BlogPosts {
    val timestamp = timestamp("12.05.2025 16:00:00")
    const val SEGMENT = "morefaster"
    const val TITLE_NO = "Mer av alt - raskere"
    const val TITLE_EN = "More of everything - faster"

    const val SUMMARY_NO = """
Hilsen energikommisjonen 2023: 
- Mer energieffektivisering.
- Mer fornybar kraft (40 TWh innen 2030).
- Mer naturødeleggende vind og solkraft.
- Mer ressursbruk.
- Mer nettutbygging.
- Mer subsidiering.
- Mer pengebruk
- Mer inflasjon
- Mer fattigdom og ulikhet

Denne tilnærmingen lukter desperasjon.
Bare sett igang raskt uten prioritering, planlegging eller prototyping.
Alle som har jobbet med IT prosjekter vet at liknende tankegang kjører alt i dass.
Det er nettopp dette som har skapt natur- og klimakrisa i utgangspunktet.
"""

    const val SUMMARY_EN = """
Greetings from the Norwegian Energy Commission 2023:
- More energy efficiency.
- More renewable power (40 TWh by 2030).
- More nature-destructive wind and solar energy.
- More resource consumption.
- More grid development.
- More subsidies.
- More spending
- More inflation
- More powerty and inequality

This approach smells of desperation.
Just rush ahead without prioritization, planning, or prototyping.
Anyone who's worked on IT projects knows this kind of thinking ruins everything.
This is exactly the mindset that created the nature and climate crisis in the first place.
"""

    override fun no(blog: Blog): BlogPost = BlogPost(timestamp, SEGMENT, TITLE_NO, SUMMARY_NO, blog)

    override fun en(blog: Blog): BlogPost = BlogPost(timestamp, SEGMENT, TITLE_EN, SUMMARY_EN, blog)
}
