package com.techreier.edrops.data.blogs.politics

import com.techreier.edrops.data.blogs.BlogPosts
import com.techreier.edrops.domain.Blog
import com.techreier.edrops.domain.BlogPost
import com.techreier.edrops.util.timestamp


object WealthTax : BlogPosts {
    val timestamp = timestamp("22.11.2025 16:56:00")
    const val SEGMENT = "wealthtax"
    const val TITLE_NO = "Særnorsk formueskatt"
    const val TITLE_EN = "Norwegian wealth tax"

    const val SUMMARY_NO = """
Staten tar inn rundt 30 milliarder i formueskatt hvert år – en ubetydelig sum.
Norske eiere må ta ut utbytte for å betale skatt på driftsmidler og bundet kapital.
Den som prøver å spare, eller sitter med litt verdier som pensjonist, får svi.
Utenlandske og offentlige eiere slipper elegant unna.
    
Resultatet? Eiere rømmer til tross for exit-skatten, skatteinntektene forsvinner,
og investeringene følger med. Flott timing for et land som desperat trenger
noe å leve av etter olje og gass, mens forbruk går foran investering.
"""

    const val SUMMARY_EN = """
The Norwegian state collects around 30 billion NOK in wealth tax each year – a negligible sum.
Norwegian owners have to take dividends to pay tax on operating assets and tied-up capital.
Anyone trying to save, or pensioners with some property, also get taxed.
Foreign and public owners escape without tax.
    
The result? Owners flee Norway despite the exit tax, tax revenues vanish,
and investments go with them. Perfect timing for a country desperately
needing something to survive after oil and gas, while consumption takes priority over investment.
"""

    override fun no(blog: Blog): BlogPost = BlogPost(timestamp, SEGMENT, TITLE_NO, SUMMARY_NO, blog)

    override fun en(blog: Blog): BlogPost = BlogPost(timestamp, SEGMENT, TITLE_EN, SUMMARY_EN, blog)

}