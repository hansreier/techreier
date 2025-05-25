package com.techreier.edrops.data.blogs.climatenv

import com.techreier.edrops.data.blogs.BlogPosts
import com.techreier.edrops.domain.Blog
import com.techreier.edrops.domain.BlogPost
import com.techreier.edrops.util.timestamp

object Green : BlogPosts {
    val timestamp = timestamp("12.05.2025 16:00:00")
    const val SEGMENT = "green"
    const val TITLE_NO = "Det grønne skiftet"
    const val TITLE_EN = "The green shift"

    const val SUMMARY_NO =
        "Grønn er en farge som finnes mye i naturen og symboliserer liv. " +
                "Det grønne skiftet handler om å redusere CO₂-utslipp, " +
                "men industriprosessene bak er ofte langt fra rene. " +
                "De krever store mengder betong, metall, grus og energi – og skaper avfall. " +
                "Natur blir skadet, og kanskje burde det heller kalles det grå skiftet."

    const val SUMMARY_EN =
        "Green is a color commonly found in nature and symbolizes life. " +
                "The green shift is about reducing CO₂ emissions, " +
                "but the industrial processes behind it are often far from clean. " +
                "They require large amounts of concrete, metal, gravel, and energy – and generate waste. " +
                "Nature is damaged, and perhaps it should be called the gray shift instead."

    override fun no(blog: Blog): BlogPost = BlogPost(timestamp, SEGMENT, TITLE_NO, SUMMARY_NO, blog)

    override fun en(blog: Blog): BlogPost = BlogPost(timestamp, SEGMENT, TITLE_EN, SUMMARY_EN, blog)
}
