package com.techreier.edrops.data.blogs.politics

import com.techreier.edrops.data.blogs.BlogPosts
import com.techreier.edrops.domain.Blog
import com.techreier.edrops.domain.BlogPost
import com.techreier.edrops.util.strip
import com.techreier.edrops.util.timestamp

object EU: BlogPosts {
    val timestamp = timestamp("30.04.2025 16:56:00")
    const val SEGMENT = "eu"
    const val TITLE_NO = "EU"
    const val TITLE_EN = "EU"

    const val SUMMARY_NO = "EU er en sammenslutning av europeiske land som samarbeider om felles lover, marked, politikk og økonomi." +
            " EU-parlamentet er det folkevalgte organet, men unionen har et stort byråkrati i Brussel. " +
            "Har EU gått for langt i detaljstyring gjennom felles lovverk og energimarked, som ikke passer alle land med ulik geografi og kultur? " +
            "Samtidig har EU bidratt til stabilitet etter andre verdenskrig."

    const val SUMMARY_EN = "The EU is a union of European countries cooperating with common laws, a shared market, politics, and economy. " +
            "The EU Parliament is the elected body, but the union has a large bureaucracy in Brussels. " +
            "Has the EU gone too far in detailed regulation through shared laws and energy market, " +
            "not suited to countries with different geography and cultures? It has helped maintain stability after World War II"

    override fun no(blog: Blog): BlogPost = BlogPost(timestamp, SEGMENT, TITLE_NO, SUMMARY_NO.strip(), blog)

    override fun en(blog: Blog): BlogPost = BlogPost(timestamp, SEGMENT, TITLE_EN, SUMMARY_EN.strip(), blog)

}