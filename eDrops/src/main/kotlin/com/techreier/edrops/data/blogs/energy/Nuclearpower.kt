package com.techreier.edrops.data.blogs.energy

import com.techreier.edrops.data.blogs.BlogPosts
import com.techreier.edrops.domain.Blog
import com.techreier.edrops.domain.BlogPost
import com.techreier.edrops.util.timestamp

object Nuclearpower: BlogPosts {
    val timestamp = timestamp("10.05.2025 16:00:00")
    const val SEGMENT = "nuclearpower"
    const val TITLE_NO = "Kjernekraft"
    const val TITLE_EN = "Nuclear power"

    const val SUMMARY_NO = "Kjernekraft gir stabil strøm uavhengig av vær, basert på spalting av uran eller thorium. " +
            "Vanlige reaktorer er store trykkvannsreaktorer, mens nye SMR-løsninger er mindre og mer fleksible. " +
            "Fordeler er høy energitetthet, trenger relativt lite areal, lave CO₂-utslipp og forutsigbar produksjon. " +
            "Ulemper er høye kostnader, lang byggetid, risiko for ulykker og utfordringer med radioaktivt avfall. " +
            "Ny teknologi kan gi sikrere og mer bærekraftige løsninger, men er ennå lite utbredt."

    const val SUMMARY_EN = "Nuclear power provides stable electricity regardless of weather, based on fission of uranium or thorium. " +
            "Common reactors are large pressurized water reactors, while new SMR designs are smaller and more flexible. " +
            "Advantages include high energy density, relatively small land use, low CO₂ emissions, and predictable output. " +
            "Drawbacks include high costs, long construction time, risk of accidents, and challenges with radioactive waste. " +
            "New technologies may offer safer and more sustainable solutions, but are not yet widely deployed."

    override fun no(blog: Blog): BlogPost = BlogPost(timestamp, SEGMENT, TITLE_NO, SUMMARY_NO, blog)

    override fun en(blog: Blog): BlogPost = BlogPost(timestamp, SEGMENT, TITLE_EN, SUMMARY_EN, blog)
}
