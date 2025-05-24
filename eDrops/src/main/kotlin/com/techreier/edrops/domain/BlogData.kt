package com.techreier.edrops.domain

import com.techreier.edrops.blogs.coding.Coding
import com.techreier.edrops.blogs.energy.Energy
import com.techreier.edrops.blogs.politics.Politics
import com.techreier.edrops.config.AppConfig
import com.techreier.edrops.config.DEFAULT_TIMEZONE
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

// Predefined segments
const val B_ENVIRONMENT = "env"
const val P_NATURE = "nature"
const val P_WEATHER = "weather"
const val P_SUSTAINABILITY = "sustainability"

const val TITLE_1X1 = "Om bærekraft"
const val TITLE_1X1E = "About sustainability"
const val TITLE_1X2 = "Om været"
const val TITLE_1X2E = "About weather"
const val TITLE_1X3 = "Om naturen"
const val TITLE_1X3E = "About nature"

const val SUBJECT1 = "Miljø saker"
const val SUBJECT1E = "Environmental issues"

const val ABOUT1 = "Om natur, miljø og klima i Norge"
const val ABOUT1E = "About nature, envirnoment and climate in Norway"

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

// Initial populate table. Temporary. Move later back to test
class BlogData(
    appConfig: AppConfig,
    base: Base,
) {
    private val blogOwnerCreated = timestamp("08.01.1963 12:00:00")
    private val datetimeb1 = timestamp("02.02.2024 13:01:24")
    private val datetime1 = timestamp("15.02.2024 15:05:30")
    private val datetime2 = timestamp("17.02.2024 15:05:30")
    private val datetime3 = timestamp("12.03.2024 11:02:00")

    private val blogPosts1 = mutableListOf<BlogPost>()
    private val blogPosts1e = mutableListOf<BlogPost>()
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

    private val blogPost1x1 = BlogPost(datetime1, P_SUSTAINABILITY, TITLE_1X1, SUMMARY_1X1, blog1)
    private val blogPost1x2 = BlogPost(datetime2, P_WEATHER, TITLE_1X2, SUMMARY_1X2, blog1)
    private val blogPost1x3 = BlogPost(datetime3, P_NATURE, TITLE_1X3, SUMMARY_1X3, blog1)
    private val blogPost1x1e = BlogPost(datetime1, P_SUSTAINABILITY, TITLE_1X1E, SUMMARY_1X1E, blog1e)
    private val blogPost1x2e = BlogPost(datetime2, P_WEATHER, TITLE_1X2E, SUMMARY_1X2E, blog1e)
    private val blogPost1x3e = BlogPost(datetime3, P_NATURE, TITLE_1X3E, SUMMARY_1X3E, blog1e)

    private val bnCoding = Coding.no(blogOwner, base.codingNo)
    private val beCoding = Coding.en(blogOwner, base.codingEn)


    private val bnPolitics = Politics.no(blogOwner, base.politicsNo)
    private val bePolitics = Politics.en(blogOwner, base.politicsEn)

    private val bnEnergy = Energy.no(blogOwner, base.energyNo)
    private val beEnergy = Energy.en(blogOwner, base.energyEn)

    init {
        initialize()
    }

    private fun initialize() {
        blogList.clear()
        blogOwner.blogs = blogList
        blogOwner.blogs.add(blog1)
        blogOwner.blogs.add(blog1e)
        blogOwner.blogs.add(bnPolitics)
        blogOwner.blogs.add(bePolitics)
        blogOwner.blogs.add(bnEnergy)
        blogOwner.blogs.add(beEnergy)
        blogOwner.blogs.add(bnCoding)
        blogOwner.blogs.add(beCoding)


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
    }

    private fun timestamp(datetime: String): Instant {
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")
        return LocalDateTime.parse(datetime, formatter).atZone(ZoneId.of(DEFAULT_TIMEZONE)).toInstant()
    }
}
