package com.techreier.edrops.blogs.energy

import com.techreier.edrops.blogs.BlogPosts
import com.techreier.edrops.domain.Blog
import com.techreier.edrops.domain.BlogPost
import com.techreier.edrops.util.timestamp

object Hydropower: BlogPosts {
    val timestamp = timestamp("12.05.2025 16:00:00")
    const val SEGMENT = "hydropower"
    const val TITLE_NO = "Vannkraft"
    const val TITLE_EN = "Hydropower"

    const val SUMMARY_NO = "Vannkraft er en fornybar og vanligvis stabil regulerbar energiressurs, men avhengig av nedbør. " +
            "Vannreservoarer, med eller uten pumpekraft, fungerer som verdens største batteri. " +
            "En vannturbin gir som regel langt mer energi enn en vindturbin. " +
            "I over hundre år har Norge fått mesteparten av sin elkraft fra billig vannkraft, " +
            "men det er ikke mange flere vassdrag som bør bygges ut. " +
            "De største naturinngrepene er neddemming og endringer i elveløp og vannstand. " +
            "Vannkraftanlegg har lang levetid, ofte over 50 år, og svært lave CO₂-utslipp."

    const val SUMMARY_EN = "Hydropower is a renewable and usually stable, adjustable energy source, but dependent on rain. " +
            "Reservoirs, with or without pumped storage, act as the world's largest battery. " +
            "A water turbine generally produces much more energy than a wind turbine. " +
            "For over a hundred years, Norway has generated most of its electricity from cheap hydropower, " +
            "but there are few rivers left to develop. " +
            "The largest environmental impacts are flooding of areas and changes to river courses and water levels. " +
            "Hydropower plants have long lifespans, often over 50 years, and very low CO₂ emissions."

    override fun no(blog: Blog): BlogPost = BlogPost(timestamp, SEGMENT, TITLE_NO, SUMMARY_NO, blog)

    override fun en(blog: Blog): BlogPost = BlogPost(timestamp, SEGMENT, TITLE_EN, SUMMARY_EN, blog)
}
