package com.techreier.edrops.domain

import com.techreier.edrops.config.AppConfig
import com.techreier.edrops.util.timeStamp
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

// Predefined segments
const val ENVIRONMENT = "env"
const val ELPOWER = "elpower"
const val WEATHER = "weather"
const val HIBERNATE = "hibernate"
const val SPRING_BOOT = "springboot"

const val TITLE_1X1 = "Om strøm"
const val TITLE_1X1E = "About electric power"
const val TITLE_1X2 = "Om været"
const val TITLE_1X2E = "About weather"
const val TITLE_1X3 = "Om Hibernate"
const val TITLE_1X3E = "About Hibernate"
const val TITLE_3X1 = "Om Spring Boot"
const val TITLE_3X1E = "About Spring Boot"
const val TITLE_2X1 = "Energi blog"
const val SUBJECT1 = "Miljø saker"
const val ABOUT1 = "Om natur, miljø og klima i Norge"
const val ABOUT1E = "About nature, envirnoment and climate in Norway"
const val SUBJECT1E = "Environmental issues"
const val SUBJECT2 = "Energi saker"
const val ABOUT2 = "Om energi og elkraft i Norge"

const val SUMMARY_1X1 =
    "Regjeringen Støre nekter å regulere strømmarkedet. " +
        "I stedet er det innført en strømstøtteordning. " +
        "Denne er brukbar for privatpersoner, men ikke for bedrifter. " +
        "Dette er dessverre bare å fikle på en dårlig ordning. " +
        "I tillegg så lekker det strøm ut via de nye høykapasitets utenlandskablene.\n\n" +
        "#Strøm #Støre"

const val SUMMARY_1X1E =
    "The government Støre refuses to control the electricity marked. " +
        "As a replacement you get some money returned back from Norwegian Authorities. " +
        "It is not that bad for private citizens, but not for companies. " +
        "This is unfortunately just tinkering with a bad setup. " +
        " In addition, electricity is leaking out through the new high-capacity foreign cables. \n\n" +
        "#ElectricalPower #Støre"

const val SUMMARY_1X2 =
    "Først var det ikke snø. " +
        "Så snødde det mye. " +
        "Så kom det masse regn. " +
        "Så ble det isglatt og iskaldt. #Snø"

const val SUMMARY_1X2E =
    "At first no snow. " +
        "Then it snowed a lot. " +
        "Then a lot of rain poured down. " +
        "Then it god icy and slippery and freezing cold. #Snø"

const val SUMMARY_1X3 =
    "Det er tydeligvis helt umulig med toveis relasjon for Hibernate på en til en relasjoner. " +
        "Jeg har gitt opp å gjøre noe med det etter mange forsøk. #Hibernate"

const val SUMMARY_1X3E =
    "It is apparently impossible with a two way relation for Hibernate on a one to one relation. " +
        "I have given up doing something about it after many attempts. #Hibernate"

const val SUMMARY_2X1 = "Dette er min nye blogg med tvilsomt innhold."

const val SUMMARY_3X1 =
    "Fordelen med Spring Boot er at det er et solid og komplett DI rammeverk. " +
            "Ulempen er at det er stort og at alle kode-konvensjonene må føges"

const val SUMMARY_3X1E =
    "The advantage with Spring Boot is that it is a solid and complete DI rammeverk " +
            "THe disadvantage is it size and that all code conventions must be followed"


// Initial populate table. Temporary. Move later back to test
class BlogData(
    appConfig: AppConfig,
    common: Common,
) {
    private val datetimeb1 = timestamp("02.02.2024 13:01:24")
    private val datetimeb2 = timestamp("02.02.2024 13:05:03")
    private val datetime1 = timestamp("15.02.2024 15:05:30")
    private val datetime2 = timestamp("17.02.2024 15:05:30")
    private val datetime3 = timestamp("12.03.2024 11:02:00")
    private val datetime2x1 = timestamp("01.01.2024 08:04:12")
    private val datetime3x1 = timestamp("01.01.2025 08:04:12")

    private val blogEntries1 = mutableListOf<BlogEntry>()
    private val blogEntries1e = mutableListOf<BlogEntry>()
    private val blogEntries2 = mutableListOf<BlogEntry>()
    private val blogEntries3 = mutableListOf<BlogEntry>()
    private val blogEntries3e = mutableListOf<BlogEntry>()
    private val blogList = mutableSetOf<Blog>()

    val blogOwner: BlogOwner =
        BlogOwner(
            timeStamp(),
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

    private val blog1 = Blog(datetime1, TOPIC_ENVIRONMENT, common.defaultNo, 2, SUBJECT1, ABOUT1, blogEntries1, blogOwner)
    private val blog1e = Blog(datetimeb1, TOPIC_ENVIRONMENT, common.defaultEn, 2, SUBJECT1E, ABOUT1E, blogEntries1e, blogOwner)
    private val blog2 = Blog(datetimeb2, TOPIC_ENERGY, common.defaultNo, 2, SUBJECT2, ABOUT2, blogEntries2, blogOwner)
    private val blog3 = Blog(datetime1, TOPIC_CODING, common.defaultNo, 2, SUBJECT1, ABOUT1, blogEntries1, blogOwner)
    private val blog3e = Blog(datetimeb1, TOPIC_CODING, common.defaultEn, 2, SUBJECT1E, ABOUT1E, blogEntries1e, blogOwner)

    private val blogEntry1x1 = BlogEntry(datetime1, ELPOWER, TITLE_1X1, SUMMARY_1X1, blog1)
    private val blogEntry1x2 = BlogEntry(datetime2, WEATHER, TITLE_1X2, SUMMARY_1X2, blog1)
    private val blogEntry1x3 = BlogEntry(datetime3, HIBERNATE, TITLE_1X3, SUMMARY_1X3, blog1)
    private val blogEntry1x1e = BlogEntry(datetime1, ELPOWER, TITLE_1X1E, SUMMARY_1X1E, blog1e)
    private val blogEntry1x2e = BlogEntry(datetime2, WEATHER, TITLE_1X2E, SUMMARY_1X2E, blog1e)
    private val blogEntry1x3e = BlogEntry(datetime3, HIBERNATE, TITLE_1X3E, SUMMARY_1X3E, blog1e)

    private val blogEntry2x1 = BlogEntry(datetime2x1, TOPIC_ENERGY, TITLE_2X1, SUMMARY_2X1, blog2)

    private val blogEntry3x1 = BlogEntry(datetime3x1, SPRING_BOOT, TITLE_3X1, SUMMARY_3X1, blog3)
    private val blogEntry3x1e = BlogEntry(datetime3x1, SPRING_BOOT, TITLE_3X1E, SUMMARY_3X1E, blog3e)

    init {
        initialize()
    }

    private fun initialize() {
        blogList.clear()
        blogOwner.blogs = blogList
        blogOwner.blogs.add(blog1)
        blogOwner.blogs.add(blog1e)
        blogOwner.blogs.add(blog2)
        blogOwner.blogs.add(blog3)
        blogOwner.blogs.add(blog3e)

        blogEntries1.clear()
        blog1.blogEntries = blogEntries1
        blog1.blogEntries.add(blogEntry1x1)
        blog1.blogEntries.add(blogEntry1x2)
        blog1.blogEntries.add(blogEntry1x3)

        blogEntries1e.clear()
        blog1e.blogEntries = blogEntries1e
        blog1e.blogEntries.add(blogEntry1x1e)
        blog1e.blogEntries.add(blogEntry1x2e)
        blog1e.blogEntries.add(blogEntry1x3e)

        blogEntries2.clear()
        blog2.blogEntries = blogEntries2
        blog2.blogEntries.add(blogEntry2x1)

        blogEntries3.clear()
        blog3.blogEntries = blogEntries3
        blog3.blogEntries.add(blogEntry3x1)

        blogEntries3e.clear()
        blog3.blogEntries = blogEntries3e
        blog3.blogEntries.add(blogEntry3x1e)
    }

    private fun timestamp(datetime: String): ZonedDateTime {
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")
        return LocalDateTime.parse(datetime, formatter).atZone(ZoneOffset.UTC)
    }
}
