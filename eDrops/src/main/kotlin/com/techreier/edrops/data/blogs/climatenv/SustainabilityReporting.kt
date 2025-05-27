package com.techreier.edrops.data.blogs.climatenv

import com.techreier.edrops.data.blogs.BlogPosts
import com.techreier.edrops.domain.Blog
import com.techreier.edrops.domain.BlogPost
import com.techreier.edrops.util.timestamp

object SustainabilityReporting : BlogPosts {
    val timestamp = timestamp("12.05.2025 16:00:00")
    const val SEGMENT = "sustainable"
    const val TITLE_NO = "Bærekraft"
    const val TITLE_EN = "SustainabilityReporting"

    const val SUMMARY_NO =
        "Bærekraft handler om å ivareta mennesker, miljø og natur, samt økonomi – nå og i framtiden. " +
                "FNs 17 bærekraftsmål dekker temaer som mennesker, miljø, natur, klima, velstand, fred og samarbeid, " +
                "men er ofte vanskelige å måle og prioritere mot hverandre. " +
                "Mange organisjoner fokuserer på to–tre mål som er mest relevante – ofte med hovedfokus på klima og CO₂-utslipp. " +
                "Hvordan havnet vi her? Eller har bærekraftsarbeidet blitt en floskelmaskin?"

    const val SUMMARY_EN =
        "Sustainability reporting is about safeguarding people, the environment and nature," +
                " as well as the economy—now and in the future. " +
                "The UN’s 17 SustainabilityReporting Development Goals cover topics such as people, environment, " +
                "nature, climate, prosperity, peace and partnerships, " +
                "but they are often difficult to measure and balance against each other. " +
                "Many organizations focus on two to three goals that are most relevant, " +
                "often with a main focus on climate and CO₂ emissions. " +
                "How did we end up here? Or has sustainability work become a buzzword machine?"

    override fun no(blog: Blog): BlogPost = BlogPost(timestamp, SEGMENT, TITLE_NO, SUMMARY_NO, blog)

    override fun en(blog: Blog): BlogPost = BlogPost(timestamp, SEGMENT, TITLE_EN, SUMMARY_EN, blog)
}
