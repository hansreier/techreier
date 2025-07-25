package com.techreier.edrops.data.blogs.energy

import com.techreier.edrops.data.blogs.BlogPosts
import com.techreier.edrops.domain.Blog
import com.techreier.edrops.domain.BlogPost
import com.techreier.edrops.util.timestamp

object Twh : BlogPosts {
    val timestamp = timestamp("24.07.2025 16:00:00")
    const val SEGMENT = "twh"
    const val TITLE_NO = "Elkraft, definisjon og måleenheter"
    const val TITLE_EN = "Electrical power, definition and units"

    const val SUMMARY_NO = """
Elektrisk kraft er energien som driver alt fra små apparater til store industrimaskiner. 
Den måles i watt (W) — for eksempel bruker en vanlig varmeovn mellom 1000 og 2000 watt.

Ved måling av store mengder energi over tid brukes terawattimer (TWh). Én TWh tilsvarer én milliard kilowattimer. 
I et normalår som 2022 brukte Norge omtrent 150 TWh strøm, mens produksjonen var noe høyere, rundt 165 TWh. 
Overskuddet eksporteres til utlandet.
"""

    const val SUMMARY_EN = """
Electrical power is the energy that powers everything from small appliances to large industries. 
It’s measured in watts (W) — for example, a typical heater uses between 1000 and 2000 watts.

For large-scale energy over time, we use terawatt-hours (TWh). One TWh equals one billion kilowatt-hours. 
In a normal year like 2022, Norway used about 150 TWh, while production was slightly higher at 165 TWh. 
The surplus is exported to other countries.
"""

    override fun no(blog: Blog): BlogPost = BlogPost(timestamp, SEGMENT, TITLE_NO, SUMMARY_NO, blog)

    override fun en(blog: Blog): BlogPost = BlogPost(timestamp, SEGMENT, TITLE_EN, SUMMARY_EN, blog)
}
