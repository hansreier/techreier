package com.techreier.edrops.domain

import com.techreier.edrops.config.AppConfig
import com.techreier.edrops.util.timeStamp
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

// Predefined segments
const val ENVIRONMENT = "env"
const val ENERGY = "energy"
const val ELPOWER = "elpower"
const val WEATHER = "weather"
const val HIBERNATE = "hibernate"

const val TITLE1 = "Om strøm"
const val TITLE1E = "About electric power"
const val TITLE2 = "Om været"
const val TITLE2E = "About weather"
const val TITLE3 = "Om Hibernate"

const val TITLE3E = "About Hibernate"
const val TITLE4 = "Energi blog"
const val SUBJECT1 = "Miljø saker"
const val ABOUT1 = "Om natur, miljø og klima i Norge"
const val ABOUT1E = "About nature, envirnoment and climate in Norway"
const val SUBJECT1E = "Environmental issues"
const val SUBJECT2 = "Energi saker"
const val ABOUT2 = "Om energi og elkraft i Norge"
const val NORWEGIAN = "Norwegian"
const val ENGLISH = "English"
const val NB = "nb"
const val EN = "en"

val Norwegian: LanguageCode = LanguageCode(NORWEGIAN, NB)
val English: LanguageCode = LanguageCode(ENGLISH, EN)

val Languages = listOf(Norwegian, English)

const val SUMMARY1 =
    "Regjeringen Støre nekter å regulere strømmarkedet. " +
        "I stedet er det innført en strømstøtteordning. " +
        "Denne er brukbar for privatpersoner, men ikke for bedrifter. " +
        "Dette er dessverre bare å fikle på en dårlig ordning. " +
        "I tillegg så lekker det strøm ut via de nye høykapasitets utenlandskablene.\n\n" +
        "#Strøm #Støre"

const val SUMMARY1E =
    "The government Støre refuses to control the electricity marked. " +
        "As a replacement you get some money returned back from Norwegian Authorities. " +
        "It is not that bad for private citizens, but not for companies. " +
        "This is unfortunately just tinkering with a bad setup. " +
        " In addition, electricity is leaking out through the new high-capacity foreign cables. \n\n" +
        "#ElectricalPower #Støre"

const val SUMMARY2 =
    "Først var det ikke snø. " +
        "Så snødde det mye. " +
        "Så kom det masse regn. " +
        "Så ble det isglatt og iskaldt. #Snø"

const val SUMMARY2E =
    "At first no snow. " +
        "Then it snowed a lot. " +
        "Then a lot of rain poured down. " +
        "Then it god icy and slippery and freezing cold. #Snø"

const val SUMMARY3 =
    "Det er tydeligvis helt umulig med toveis relasjon for Hibernate på en til en relasjoner. " +
        "Jeg har gitt opp å gjøre noe med det etter mange forsøk. #Hibernate"

const val SUMMARY3E =
    "It is apparently impossible with a two way relation for Hibernate on a one to one relation. " +
        "I have given up doing something about it after many attempts. #Hibernate"

const val SUMMARY4 = "Dette er min nye blogg med tvilsomt innhold."

// Initial populate table. Temporary. Move later back to test
data class BlogData(
    val appConfig: AppConfig,
) {
    private val datetimeb1 = timestamp("02.02.2024 13:01:24")
    private val datetimeb2 = timestamp("02.02.2024 13:05:03")
    private val datetime1 = timestamp("15.02.2024 15:05:30")
    private val datetime2 = timestamp("17.02.2024 15:05:30")
    private val datetime3 = timestamp("12.03.2024 11:02:00")
    private val datetime23 = timestamp("01.01.2024 08:04:12")

    private val blogEntries1 = mutableListOf<BlogEntry>()
    private val blogEntries1e = mutableListOf<BlogEntry>()
    private val blogEntries2 = mutableListOf<BlogEntry>()
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

    // Predefined topics.
    val defaultNo = Topic(Topic.DEFAULT, Norwegian)
    val defaultEn = Topic(Topic.DEFAULT, English)
    val codingNo = Topic(Topic.CODING, Norwegian)
    val codingEn = Topic(Topic.CODING, English)
    val energyNo = Topic(Topic.ENERGY, Norwegian)
    val energyEn = Topic(Topic.ENERGY, English)

    val topics = listOf(defaultNo, defaultEn, codingNo, codingEn, energyNo, energyEn)

    //  val a = Topics.CODING_EN.create()
    private val blog1 = Blog(datetime1, ENVIRONMENT, defaultNo, 1, SUBJECT1, ABOUT1, blogEntries1, blogOwner)
    private val blog1e = Blog(datetimeb1, ENVIRONMENT, defaultEn, 1, SUBJECT1E, ABOUT1E, blogEntries1e, blogOwner)
    private val blog2 = Blog(datetimeb2, ENERGY, defaultNo, 2, SUBJECT2, ABOUT2, blogEntries2, blogOwner)

    private val blogEntry1 = BlogEntry(datetime1, ELPOWER, TITLE1, SUMMARY1, blog1)
    private val blogEntry2 = BlogEntry(datetime2, WEATHER, TITLE2, SUMMARY2, blog1)
    private val blogEntry3 = BlogEntry(datetime3, HIBERNATE, TITLE3, SUMMARY3, blog1)
    private val blogEntry1e = BlogEntry(datetime1, ELPOWER, TITLE1E, SUMMARY1E, blog1e)
    private val blogEntry2e = BlogEntry(datetime2, WEATHER, TITLE2E, SUMMARY2E, blog1e)
    private val blogEntry3e = BlogEntry(datetime3, HIBERNATE, TITLE3E, SUMMARY3E, blog1e)
    private val blog2Entry3 = BlogEntry(datetime23, ENERGY, TITLE4, SUMMARY4, blog2)

    init {
        initialize()
    }

    private fun initialize() {
        blogList.clear()
        blogOwner.blogs = blogList
        blogOwner.blogs.add(blog1)
        blogOwner.blogs.add(blog1e)
        blogOwner.blogs.add(blog2)

        blogEntries1.clear()
        blog1.blogEntries = blogEntries1
        blog1.blogEntries.add(blogEntry1)
        blog1.blogEntries.add(blogEntry2)
        blog1.blogEntries.add(blogEntry3)

        blogEntries1e.clear()
        blog1e.blogEntries = blogEntries1e
        blog1e.blogEntries.add(blogEntry1e)
        blog1e.blogEntries.add(blogEntry2e)
        blog1e.blogEntries.add(blogEntry3e)

        blogEntries2.clear()
        blog2.blogEntries = blogEntries2
        blog2.blogEntries.add(blog2Entry3)
    }

    private fun timestamp(datetime: String): ZonedDateTime {
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")
        return LocalDateTime.parse(datetime, formatter).atZone(ZoneOffset.UTC)
    }
}
