package com.techreier.edrops.data.blogs.climatenv

import com.techreier.edrops.data.blogs.BlogPosts
import com.techreier.edrops.domain.Blog
import com.techreier.edrops.domain.BlogPost
import com.techreier.edrops.util.strip
import com.techreier.edrops.util.timestamp

object Co2goal : BlogPosts {
    val timestamp = timestamp("14.06.2025 10:00:00")
    const val SEGMENT = "c02reduction"
    const val TITLE_NO = "C0₂ reduksjon mål 2035 Norge"
    const val TITLE_EN = "C0₂ reduction goal 2035 Norway"

    const val SUMMARY_NO = """
CO₂-reduksjon fra 1990 til 2024 var på 12,4 % i Norge.
Det var et stortingsvedtak om 55 % reduksjon til 2035.
Dette målet er nå skjerpet til 75 %.
Ekstrapolerer vi i henhold til tallene fra SSB, får vi 16,4 %.
Og de tar ikke engang med skogen i regnskapet, men inkluderer CO₂-kvoter.

***Dette er galskap og ønsketenkning, skapt av inkompetente politikere.***
"""

    const val SUMMARY_EN = """
CO₂ reduction from 1990 to 2024 was 12.4% in Norway.
There was a parliamentary decision to reduce emissions by 55% by 2035.
This target has now been tightened to 75%.
If we extrapolate according to figures from Statistics Norway (SSB), we get 16.4%.
And they don't even include the forest in the accounting, but include CO₂ quotas.

***This is madness and wishful thinking, crated by incompetent politicians.***
"""

    override fun no(blog: Blog): BlogPost = BlogPost(timestamp, SEGMENT, TITLE_NO, SUMMARY_NO.strip(), blog)

    override fun en(blog: Blog): BlogPost = BlogPost(timestamp, SEGMENT, TITLE_EN, SUMMARY_EN.strip(), blog)
}
