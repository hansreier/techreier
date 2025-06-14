package com.techreier.edrops.data.blogs.climatenv

import com.techreier.edrops.data.blogs.BlogPosts
import com.techreier.edrops.domain.Blog
import com.techreier.edrops.domain.BlogPost
import com.techreier.edrops.util.timestamp

object Co2quotas : BlogPosts {
    val timestamp = timestamp("14.06.2025 09:00:00")
    const val SEGMENT = "c02quotas"
    const val TITLE_NO = "CO₂-kvoter,  hokus pokus"
    const val TITLE_EN = "CO₂-quotas,  hocus pocus"

    const val SUMMARY_NO = """
Norge har høy CO₂-modenhet – omtrent 50–60 % av energiforbruket er uten utslipp. 
Likevel forventes like store kutt som i land som fortsatt er avhengige av olje, kull og gass. 
Heldigvis kan vi kjøpe CO₂ kvoter – og det er ikke nødvendigvis feil i globalt system. 
Men hvis alle gjør det uten reelle kutt, ender vi i Catch-22.
"""

    const val SUMMARY_EN = """
Norway has high CO₂ maturity – about 50–60 % of energy consumption is emission-free. 
Yet similar cuts are expected as in countries still dependent on oil, coal, and gas. 
Fortunately, we can buy CO₂ quotas – which is not necessarily wrong in global system. 
But if everyone does so without real cuts, we end up in Catch-22.
"""

    override fun no(blog: Blog): BlogPost = BlogPost(timestamp, SEGMENT, TITLE_NO, SUMMARY_NO, blog)

    override fun en(blog: Blog): BlogPost = BlogPost(timestamp, SEGMENT, TITLE_EN, SUMMARY_EN, blog)
}
