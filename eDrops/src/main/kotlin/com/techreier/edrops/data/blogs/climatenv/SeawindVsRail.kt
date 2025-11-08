package com.techreier.edrops.data.blogs.climatenv

import com.techreier.edrops.data.blogs.BlogPosts
import com.techreier.edrops.domain.Blog
import com.techreier.edrops.domain.BlogPost
import com.techreier.edrops.util.timestamp

object SeawindVsRail : BlogPosts {
    val timestamp = timestamp("31.08.2025 12:00:00")
    const val SEGMENT = "seawindvsrail"
    const val TITLE_NO = "Jernbane vs flytende havvind"
    const val TITLE_EN = "Rail vs floating offshore wind"

    const val SUMMARY_NO = """
I Norge er 2025 statsbudjettet på 35 mrd kroner for flytende havvind og 32,6 mrd kroner på jernbaneinfrastruktur.
Flytende havvind vil produsere ca. 10 TWh/år. Jernbane frakter millioner av passasjerer og sparer energi sammenlignet med bil og fly,
med en årlig CO2-besparelse på 140 000 tonn.

Jernbaneutbygging vil ha tidligst effekt 2027–2030, mens havvind tidligst gir effekt 2030–2035.
Et økt jernbanebudsjett forbedrer kapasitet, punktlighet og energisparing, mens satsing på havvind gir primært økt energitilgang.  

***Doble jernbanebudsjettet i stedet for havvind satningen!?***
"""

    const val SUMMARY_EN = """
In Norway, the 2025 state budget is NOK 35 billion for floating offshore wind and NOK 32.6 billion for railway infrastructure.
Floating offshore wind will produce about 10 TWh/year. Railways carry millions of passengers and save energy compared to car and air travel,
with annual CO2 savings of around 140,000 tons.

Rail expansion will have its earliest possible effect 2027–2030, while offshore wind will earliest deliver effect 2030–2035.
An increased rail budget improves capacity, punctuality, and energy efficiency, while offshore wind mainly increases energy supply.   

***Double the rail budget instead of the offshore wind spending!?***
"""


    override fun no(blog: Blog): BlogPost = BlogPost(timestamp, SEGMENT, TITLE_NO, SUMMARY_NO, blog)

    override fun en(blog: Blog): BlogPost = BlogPost(timestamp, SEGMENT, TITLE_EN, SUMMARY_EN, blog)

}