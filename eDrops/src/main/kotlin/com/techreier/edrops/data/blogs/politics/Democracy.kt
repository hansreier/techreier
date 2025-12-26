package com.techreier.edrops.data.blogs.politics

import com.techreier.edrops.data.blogs.BlogPosts
import com.techreier.edrops.domain.Blog
import com.techreier.edrops.domain.BlogPost
import com.techreier.edrops.util.strip
import com.techreier.edrops.util.timestamp

object Democracy: BlogPosts {
    val timestamp = timestamp("01.05.2025 13:56:00")
    const val SEGMENT = "democracy"
    const val TITLE_NO = "Demokrati"
    const val TITLE_EN = "Democracy"

    const val SUMMARY_NO = "Et demokrati er et styresett der befolkningen har reell medbestemmelsesrett og folkeflertallet bestemmer, " +
            " enten direkte eller indirekte. Det er ikke demokrati når politiske tiltak blir satt igang uten reell medbestemmelse. "

    const val SUMMARY_EN ="Democracy is a model of governance in which the population has meaningful influence where the majority rules, " +
            " either directly or indirectly. It is not a democracy when political decisions are implemented without genuine public involvement."

    override fun no(blog: Blog): BlogPost = BlogPost(timestamp, SEGMENT, TITLE_NO, SUMMARY_NO.strip(), blog)

    override fun en(blog: Blog): BlogPost = BlogPost(timestamp, SEGMENT, TITLE_EN, SUMMARY_EN.strip(), blog)
}
