package com.techreier.edrops.data.blogs.energy

import com.techreier.edrops.data.blogs.BlogPosts
import com.techreier.edrops.domain.Blog
import com.techreier.edrops.domain.BlogPost
import com.techreier.edrops.util.timestamp

object Dieselpower: BlogPosts {
    val timestamp = timestamp("12.05.2025 16:00:00")
    const val SEGMENT = "dieselpower"
    const val TITLE_NO = "Diesel-kraft"
    const val TITLE_EN = "Diesel-power"

    const val SUMMARY_NO = "Dieselkraft er en fossil energikilde som slipper ut mer CO2 enn gass. " +
            "Den brukes sjelden til ordinær strømproduksjon i Norge, men er ofte en midlertidig løsning " +
            "der strøm er vanskelig tilgjengelig eller midlertidig dyrt. " +
            "Diesel brukes også i større grad i andre land med dårligere infrastruktur."

    const val SUMMARY_EN = "Diesel power is a fossil energy source that emits more CO2 than gas. " +
            "It is rarely used for regular electricity production in Norway but often serves as a temporary solution " +
            "where electricity is hard to access or temporarily expensive. " +
            "Diesel is also more commonly used in countries with less developed infrastructure."

    override fun no(blog: Blog): BlogPost = BlogPost(timestamp, SEGMENT, TITLE_NO, SUMMARY_NO, blog)

    override fun en(blog: Blog): BlogPost = BlogPost(timestamp, SEGMENT, TITLE_EN, SUMMARY_EN, blog)
}
