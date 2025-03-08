package com.techreier.edrops.domain

import com.techreier.edrops.config.AppConfig
import com.techreier.edrops.util.timeStamp
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

// Predefined segments
const val BSEG_CODING = "coding"
const val BSEG_ENVIRONMENT = "env"
const val BSEG_ENERGY = "energy"

const val ESEG_ELPOWER = "elpower"
const val ESEG_NATURE = "nature"
const val ESEG_WEATHER = "weather"
const val ESEG_SUSTAINABILITY = "sustainability"
const val ESEG_HIBERNATE = "hibernate"
const val ESEG_SPRING_BOOT = "springboot"

const val TITLE_1X1 = "Om bærekraft"
const val TITLE_1X1E = "About sustainability"
const val TITLE_1X2 = "Om været"
const val TITLE_1X2E = "About weather"
const val TITLE_1X3 = "Om naturen"
const val TITLE_1X3E = "About nature"
const val TITLE_3X1 = "Om Spring Boot"
const val TITLE_3X1E = "About Spring Boot"
const val TITLE_2X1 = "Energi blog"
const val TITLE_2X2 = "Elkraft blog"
const val TITLE_3X2 = "Om Hibernate"
const val TITLE_3X2E = "About Hibernate"

const val SUBJECT1 = "Miljø saker"
const val SUBJECT1E = "Environmental issues"
const val SUBJECT2 = "Energi saker"
const val SUBJECT3 = "Programmerings saker"
const val SUBJECT3E = "Programming stuff"

const val ABOUT1 = "Om natur, miljø og klima i Norge"
const val ABOUT1E = "About nature, envirnoment and climate in Norway"
const val ABOUT2 = "Om energi og elkraft i Norge"
const val ABOUT3 = "Om koding"
const val ABOUT3E = "About coding"

const val SUMMARY_1X1 =
    "FN har 17 bærekraftmål der alle er like viktig "

const val SUMMARY_1X1E =
    "FN har 17 sustainability goals where all are equally important"

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

const val SUMMARY_1X3 = "Jeg vil bevare  natur, ikke fylle dem med vindturbiner"

const val SUMMARY_1X3E = "I want to preserve nature, not deatroy nature with wind turbines"

const val SUMMARY_2X1 = "Dette er min nye blogg hvordan vi skal få nok energi og lite utslipp"
const val SUMMARY_2X2 = "Dette er min nye blogg om elkraftproduksjon uten vindturbinindustriparker."

const val SUMMARY_3X1 =
    "Fordelen med Spring Boot er at det er et solid og komplett DI rammeverk. " +
        "Ulempen er at det er stort og at alle kode-konvensjonene må føges"

const val SUMMARY_3X1E =
    "The advantage with Spring Boot is that it is a solid and complete DI rammeverk " +
        "THe disadvantage is it size and that all code conventions must be followed"

const val SUMMARY_3X2 =
    "Det er tydeligvis helt umulig med toveis relasjon for Hibernate på en til en relasjoner. " +
        "Jeg har gitt opp å gjøre noe med det etter mange forsøk. #Hibernate"

const val SUMMARY_3X2E =
    "It is apparently impossible with a two way relation for Hibernate on a one to one relation. " +
        "I have given up doing something about it after many attempts. #Hibernate"

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
    private val datetime2x2 = timestamp("01.01.2025 08:04:12")
    private val datetime3x1 = timestamp("01.01.2025 08:04:12")
    private val datetime3x2 = timestamp("01.02.2025 08:04:12")

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

    // Note: Adding duplicate entries will result in unique result exception when reading the blog.
    // The problem is how to recover without risking recursion.
    // A redirect to homepage with a warning message added is one possibility.
    // The problem is that have to check for type of DB error, a general type of DB failure should not return to homepage
    // since even the menu uses the database.
    // TODO: Must at least be taken care of in save blog GUI. Using an array or list here could allow for adding check
    private val blog1 = Blog(datetime1, BSEG_ENVIRONMENT, common.defaultNo, 2, SUBJECT1, ABOUT1, blogEntries1, blogOwner)
    private val blog1e = Blog(datetimeb1, BSEG_ENVIRONMENT, common.defaultEn, 2, SUBJECT1E, ABOUT1E, blogEntries1e, blogOwner)
    private val blog2 = Blog(datetimeb2, BSEG_ENERGY, common.energyNo, 2, SUBJECT2, ABOUT2, blogEntries2, blogOwner)
    private val blog3 = Blog(datetime1, BSEG_CODING, common.codingNo, 2, SUBJECT3, ABOUT3, blogEntries3, blogOwner)
    private val blog3e = Blog(datetimeb1, BSEG_CODING, common.codingEn, 2, SUBJECT3E, ABOUT3E, blogEntries3e, blogOwner)

    private val blogEntry1x1 = BlogEntry(datetime1, ESEG_SUSTAINABILITY, TITLE_1X1, SUMMARY_1X1, blog1)
    private val blogEntry1x2 = BlogEntry(datetime2, ESEG_WEATHER, TITLE_1X2, SUMMARY_1X2, blog1)
    private val blogEntry1x3 = BlogEntry(datetime3, ESEG_NATURE, TITLE_1X3, SUMMARY_1X3, blog1)
    private val blogEntry1x1e = BlogEntry(datetime1, ESEG_SUSTAINABILITY, TITLE_1X1E, SUMMARY_1X1E, blog1e)
    private val blogEntry1x2e = BlogEntry(datetime2, ESEG_WEATHER, TITLE_1X2E, SUMMARY_1X2E, blog1e)
    private val blogEntry1x3e = BlogEntry(datetime3, ESEG_NATURE, TITLE_1X3E, SUMMARY_1X3E, blog1e)

    private val blogEntry2x1 = BlogEntry(datetime2x1, BSEG_ENERGY, TITLE_2X1, SUMMARY_2X1, blog2)
    private val blogEntry2x2 = BlogEntry(datetime2x2, ESEG_ELPOWER, TITLE_2X2, SUMMARY_2X2, blog2)

    private val blogEntry3x1 = BlogEntry(datetime3x1, ESEG_SPRING_BOOT, TITLE_3X1, SUMMARY_3X1, blog3)
    private val blogEntry3x2 = BlogEntry(datetime3x2, ESEG_HIBERNATE, TITLE_3X2, SUMMARY_3X2, blog3)
    private val blogEntry3x1e = BlogEntry(datetime3x1, ESEG_SPRING_BOOT, TITLE_3X1E, SUMMARY_3X1E, blog3e)
    private val blogEntry3x2e = BlogEntry(datetime3x2, ESEG_HIBERNATE, TITLE_3X2E, SUMMARY_3X2E, blog3e)

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
        blog2.blogEntries.add(blogEntry2x2)

        blogEntries3.clear()
        blog3.blogEntries = blogEntries3
        blog3.blogEntries.add(blogEntry3x1)
        blog3.blogEntries.add(blogEntry3x2)

        blogEntries3e.clear()
        blog3e.blogEntries = blogEntries3e
        blog3e.blogEntries.add(blogEntry3x1e)
        blog3e.blogEntries.add(blogEntry3x2e)
    }

    private fun timestamp(datetime: String): ZonedDateTime {
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")
        return LocalDateTime.parse(datetime, formatter).atZone(ZoneOffset.UTC)
    }
}
