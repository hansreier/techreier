package com.techreier.edrops.data.blogs.climatenv

import com.techreier.edrops.data.blogs.BlogPosts
import com.techreier.edrops.domain.Blog
import com.techreier.edrops.domain.BlogPost
import com.techreier.edrops.util.strip
import com.techreier.edrops.util.timestamp

object SustainabilityReporting : BlogPosts {
    val timestamp = timestamp("29.05.2025 16:00:00")
    const val SEGMENT = "sustainreporting"
    const val TITLE_NO = "Bærekraftsrapportering"
    const val TITLE_EN = "Sustainability reporting"

    const val SUMMARY_NO = """
Bærekraftsrapportering betyr at selskaper rapporterer om miljømessige, 
sosiale og styringsmessige forhold (ESG), med utgangspunkt i FNs bærekraftsmål.
I EU og Norge er dette regulert av CSRD (Corporate Sustainability Reporting Directive), 
som gjelder for store selskaper, med rapportering etter ESRS-standardene.

Mange organisasjoner fokuserer på to–tre mål – ofte klima og CO₂-utslipp. 
Hvordan havnet vi her? Eller har bærekraftsrapportering blitt en floskelmaskin?
"""

    const val SUMMARY_EN = """
Sustainability reporting means that companies report on environmental, 
social, and governance (ESG) issues, based on the UN Sustainable Development Goals.
In the EU and Norway, this is regulated by the CSRD (Corporate Sustainability Reporting Directive), 
which applies to large companies, requiring reporting according to the ESRS standards.

Many organizations focus on two or three goals—often climate and CO₂ emissions. 
How did we end up here? Or has sustainability reporting become a buzzword machine?
"""

    override fun no(blog: Blog): BlogPost = BlogPost(timestamp, SEGMENT, TITLE_NO, SUMMARY_NO.strip(), blog)

    override fun en(blog: Blog): BlogPost = BlogPost(timestamp, SEGMENT, TITLE_EN, SUMMARY_EN.strip(), blog)
}
