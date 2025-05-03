package com.techreier.edrops.domain

import com.techreier.edrops.blogs.Politics
import com.techreier.edrops.config.AppConfig
import com.techreier.edrops.config.DEFAULT_TIMEZONE
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

// Predefined segments
const val B_CODING = "coding"
const val B_ENVIRONMENT = "env"
const val B_ENERGY = "energy"

const val P_ELPOWER = "elpower"
const val P_NATURE = "nature"
const val P_WEATHER = "weather"
const val P_SUSTAINABILITY = "sustainability"
const val P_HIBERNATE = "hibernate"
const val P_SPRING_BOOT = "springboot"

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
    base: Base,
) {
    private val blogOwnerCreated = timestamp("08.01.1963 12:00:00")
    private val datetimeb1 = timestamp("02.02.2024 13:01:24")
    private val datetimeb2 = timestamp("02.02.2024 13:05:03")
    private val datetime1 = timestamp("15.02.2024 15:05:30")
    private val datetime2 = timestamp("17.02.2024 15:05:30")
    private val datetime3 = timestamp("12.03.2024 11:02:00")
    private val datetime2x1 = timestamp("01.01.2024 08:04:12")
    private val datetime2x2 = timestamp("01.01.2025 08:04:12")
    private val datetime3x1 = timestamp("01.01.2025 08:04:12")
    private val datetime3x2 = timestamp("01.02.2025 08:04:12")

    private val blogPosts1 = mutableListOf<BlogPost>()
    private val blogPosts1e = mutableListOf<BlogPost>()
    private val blogPosts2 = mutableListOf<BlogPost>()
    private val blogPosts3 = mutableListOf<BlogPost>()
    private val blogPosts3e = mutableListOf<BlogPost>()
    private val bpnPolitics = mutableListOf<BlogPost>()
    private val bpePolitics = mutableListOf<BlogPost>()
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

    // Note: Adding duplicate posts will result in unique result exception when reading the blog.
    // The problem is how to recover without risking recursion.
    // A redirect to homepage with a warning message added is one possibility.
    // The problem is that have to check for type of DB error, a general type of DB failure should not return to homepage
    // since even the menu uses the database.
    // TODO: Must at least be taken care of in save blog GUI. Using an array or list here could allow for adding check
    private val blog1 = Blog(datetime1, B_ENVIRONMENT, base.defaultNo, 2, SUBJECT1, ABOUT1, blogPosts1, blogOwner)
    private val blog1e = Blog(datetimeb1, B_ENVIRONMENT, base.defaultEn, 2, SUBJECT1E, ABOUT1E, blogPosts1e, blogOwner)
    private val blog2 = Blog(datetimeb2, B_ENERGY, base.energyNo, 2, SUBJECT2, ABOUT2, blogPosts2, blogOwner)
    private val blog3 = Blog(datetime1, B_CODING, base.codingNo, 2, SUBJECT3, ABOUT3, blogPosts3, blogOwner)
    private val blog3e = Blog(datetimeb1, B_CODING, base.codingEn, 2, SUBJECT3E, ABOUT3E, blogPosts3e, blogOwner)

    private val blogPost1x1 = BlogPost(datetime1, P_SUSTAINABILITY, TITLE_1X1, SUMMARY_1X1, blog1)
    private val blogPost1x2 = BlogPost(datetime2, P_WEATHER, TITLE_1X2, SUMMARY_1X2, blog1)
    private val blogPost1x3 = BlogPost(datetime3, P_NATURE, TITLE_1X3, SUMMARY_1X3, blog1)
    private val blogPost1x1e = BlogPost(datetime1, P_SUSTAINABILITY, TITLE_1X1E, SUMMARY_1X1E, blog1e)
    private val blogPost1x2e = BlogPost(datetime2, P_WEATHER, TITLE_1X2E, SUMMARY_1X2E, blog1e)
    private val blogPost1x3e = BlogPost(datetime3, P_NATURE, TITLE_1X3E, SUMMARY_1X3E, blog1e)

    private val blogPost2x1 = BlogPost(datetime2x1, B_ENERGY, TITLE_2X1, SUMMARY_2X1, blog2)
    private val blogPost2x2 = BlogPost(datetime2x2, P_ELPOWER, TITLE_2X2, SUMMARY_2X2, blog2)

    private val blogPost3x1 = BlogPost(datetime3x1, P_SPRING_BOOT, TITLE_3X1, SUMMARY_3X1, blog3)
    private val blogPost3x2 = BlogPost(datetime3x2, P_HIBERNATE, TITLE_3X2, SUMMARY_3X2, blog3)
    private val blogPost3x1e = BlogPost(datetime3x1, P_SPRING_BOOT, TITLE_3X1E, SUMMARY_3X1E, blog3e)
    private val blogPost3x2e = BlogPost(datetime3x2, P_HIBERNATE, TITLE_3X2E, SUMMARY_3X2E, blog3e)


    private val bnPolitics = Politics.no(blogOwner, base.codingNo, bpnPolitics)
    private val bePolitics = Politics.en(blogOwner, base.codingEn, bpePolitics)

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
        blogOwner.blogs.add(bnPolitics)
        blogOwner.blogs.add(bePolitics)

        blogPosts1.clear()
        blog1.blogPosts = blogPosts1
        blog1.blogPosts.add(blogPost1x1)
        blog1.blogPosts.add(blogPost1x2)
        blog1.blogPosts.add(blogPost1x3)

        blogPosts1e.clear()
        blog1e.blogPosts = blogPosts1e
        blog1e.blogPosts.add(blogPost1x1e)
        blog1e.blogPosts.add(blogPost1x2e)
        blog1e.blogPosts.add(blogPost1x3e)

        blogPosts2.clear()
        blog2.blogPosts = blogPosts2
        blog2.blogPosts.add(blogPost2x1)
        blog2.blogPosts.add(blogPost2x2)

        blogPosts3.clear()
        blog3.blogPosts = blogPosts3
        blog3.blogPosts.add(blogPost3x1)
        blog3.blogPosts.add(blogPost3x2)

        blogPosts3e.clear()
        blog3e.blogPosts = blogPosts3e
        blog3e.blogPosts.add(blogPost3x1e)
        blog3e.blogPosts.add(blogPost3x2e)
    }

    private fun timestamp(datetime: String): Instant {
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")
        return LocalDateTime.parse(datetime, formatter).atZone(ZoneId.of(DEFAULT_TIMEZONE)).toInstant()
    }
}
