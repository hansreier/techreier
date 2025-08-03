package com.techreier.edrops.data.blogs.coding

import com.techreier.edrops.data.blogs.BlogPosts
import com.techreier.edrops.domain.Blog
import com.techreier.edrops.domain.BlogPost
import com.techreier.edrops.util.timestamp

object ZeroTrust : BlogPosts {
    val timestamp = timestamp("08.03.2025 16:00:00")
    const val SEGMENT = "zerotrust"
    const val TITLE_NO = "Zero trust - Når sikkerhet blir et problem i seg selv."
    const val TITLE_EN = "Zero trust - When security in itself becomes a problem."

    const val SUMMARY_NO = """
\- Jeg har brukt timer på å vente på en tilgang jeg ikke burde trenge og som jeg ikke forstår.  
\- Tjenesten feiler på grunn av en blokkering ingen helt forstår.  
\- Jeg må rette opp i sikkerhetshull som ikke eksponeres i koden, bare for å få den godkjent.  
\- Det jeg trenger, finnes dessverre på et nettsted som er sperret. 
        
[Les mer om Zero Trust og hvorfor dette skjer.](../about/zerotrust) 
"""

    const val SUMMARY_EN = """
\- I’ve spent hours waiting for access I shouldn’t need and don’t understand.  
\- The service fails due to a security block no one fully grasps.  
\- I’m forced to patch security issues that aren’t exposed in the code, just to get approval.  
\- And what I actually need is on a website that’s blocked.  

[Read more about Zero Trust and why this happens.](../about/zerotrust) 
"""

    override fun no(blog: Blog): BlogPost = BlogPost(timestamp, SEGMENT, TITLE_NO, SUMMARY_NO, blog)

    override fun en(blog: Blog): BlogPost = BlogPost(timestamp, SEGMENT, TITLE_EN, SUMMARY_EN, blog)

}