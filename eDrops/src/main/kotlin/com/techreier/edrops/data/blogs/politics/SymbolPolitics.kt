package com.techreier.edrops.data.blogs.politics

import com.techreier.edrops.data.blogs.BlogPosts
import com.techreier.edrops.domain.Blog
import com.techreier.edrops.domain.BlogPost
import com.techreier.edrops.util.timestamp

object SymbolPolitics: BlogPosts {
    val timestamp = timestamp("30.04.2025 16:56:00")
    const val SEGMENT = "symbol"
    const val TITLE_NO = "Symbol politikk"
    const val TITLE_EN = "Symbol politics"

    const val SUMMARY_NO = "Symbolpolitikk er et politisk tiltak som i praksis ikke gir ønsket effekt," +
            " fordi det enten ikke lar seg gjennomføre, fungerer dårlig, eller blir altfor kostbart. " +
            " Eksempler kan være vindturbiner til havs, elektrifisering av Melkøya eller nullutslipps-soner i byer."

    const val SUMMARY_EN = "A showpiece policy is a political measure that in practice does not achieve the intended effect," +
            " because it either cannot be implemented, functions poorly, or becomes far too costly. " +
            " Examples include offshore wind turbines, electrification of the Norwegian gas processing plant Melkøya," +
            " or zero-emission zones in cities."

    override fun no(blog: Blog): BlogPost = BlogPost(timestamp, SEGMENT, TITLE_NO, SUMMARY_NO, blog)

    override fun en(blog: Blog): BlogPost = BlogPost(timestamp, SEGMENT, TITLE_EN, SUMMARY_EN, blog)

}