package com.techreier.edrops.domain

import com.techreier.edrops.blogs.climatenv.Climatenv
import com.techreier.edrops.blogs.coding.Coding
import com.techreier.edrops.blogs.energy.Energy
import com.techreier.edrops.blogs.politics.Politics
import com.techreier.edrops.config.AppConfig
import com.techreier.edrops.config.DEFAULT_TIMEZONE
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

const val SUMMARY_1X1 =
    "FN har 17 bærekraftmål der alle er like viktig "


const val SUMMARY_1X2 =
    "Først var det ikke snø. " +
            "Så snødde det mye. " +
            "Så kom det masse regn. " +
            "Så ble det isglatt og iskaldt. #Snø"


class BlogData(
    appConfig: AppConfig,
    base: Base,
) {
    private val blogOwnerCreated = timestamp("08.01.1963 12:00:00")

    private val blogList = mutableSetOf<Blog>()

    val blogOwner: BlogOwner =
        BlogOwner(
            blogOwnerCreated,
            null,
            appConfig.user,
            appConfig.password,
            "Hans Reier",
            "Sigmond",
            "reier.sigmond@gmail.com",
            "+4791668863",
            "Sløttvegen 17",
            "2390",
            "Moelv",
            "NO",
            blogList,
        )

    private val bnCoding = Coding.no(blogOwner, base.codingNo)
    private val beCoding = Coding.en(blogOwner, base.codingEn)

    private val bnPolitics = Politics.no(blogOwner, base.politicsNo)
    private val bePolitics = Politics.en(blogOwner, base.politicsEn)

    private val bnEnergy = Energy.no(blogOwner, base.energyNo)
    private val beEnergy = Energy.en(blogOwner, base.energyEn)

    private val bnClimatenv = Climatenv.no(blogOwner, base.energyNo)
    private val beClimatenv = Climatenv.en(blogOwner, base.energyEn)

    init {
        initialize()
    }

    private fun initialize() {
        blogList.clear()
        blogOwner.blogs = blogList
        blogOwner.blogs.add(bnPolitics)
        blogOwner.blogs.add(bePolitics)
        blogOwner.blogs.add(bnEnergy)
        blogOwner.blogs.add(beEnergy)
        blogOwner.blogs.add(bnCoding)
        blogOwner.blogs.add(beCoding)
        blogOwner.blogs.add(bnClimatenv)
        blogOwner.blogs.add(beClimatenv)
    }

    private fun timestamp(datetime: String): Instant {
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")
        return LocalDateTime.parse(datetime, formatter).atZone(ZoneId.of(DEFAULT_TIMEZONE)).toInstant()
    }
}
