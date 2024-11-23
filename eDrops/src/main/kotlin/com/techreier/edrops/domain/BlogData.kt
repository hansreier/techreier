package com.techreier.edrops.domain

import com.techreier.edrops.config.AppConfig
import org.springframework.stereotype.Component
import java.time.LocalDateTime

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

@JvmField
val Norwegian: LanguageCode = LanguageCode(NORWEGIAN, NB)

@JvmField
val English: LanguageCode = LanguageCode(ENGLISH, EN)

val Languages = listOf(Norwegian, English)

const val SUMMARY1 = """
    Regjeringen Støre nekter å regulere strømmarkedet.
    I stedet er det innført en strømstøtteordning.
    Denne er brukbar for privatpersoner, men ikke for bedrifter.
    Dette er dessverre bare å fikle på en dårlig ordning.
    I tillegg så lekker det strøm ut via de nye høykapasitets utenlandskablene.
    #Strøm #Støre
    """
const val SUMMARY1E = """
    The government Støre refuses to control the electricity marked.
    As a replacement you get some money returned back from Norwegian Authorities.
    It is not that bad for private citizens, but not for companies.
    This is unfortunately just tinkering with a bad setup.
    In addition, electricity is leaking out through the new high-capacity foreign cables.
    #ElectricalPower #Støre
    """
const val SUMMARY2 = """
    Først var det ikke snø,
    Så snødde det mye.
    Så kom det masse regn.
    Så ble det isglatt og iskaldt.
    #Snø
    """

const val SUMMARY2E = """
    At first no snow
    Then it snowed a lot,
    Then a lot of rain poured down.
    Then it god icy and slippery and freezing cold.
    #Snow
    """

const val SUMMARY3 = """
    Det er tydeligvis helt umulig med toveis relasjon for Hibernate på en til en relasjoner.
    Jeg har gitt opp å gjøre noe med det etter mange forsøk.
    #Hibernate
    """
const val SUMMARY3E = """
    It is apparently impossible with a two way relation for Hibernate on a one to one relation.
    I have given up doing something about it after many attempts.
    #Hibernate
    """

const val SUMMARY4 = """
    Dette er min nye blogg med tvilsomt innhold.
    """

// Initial populate table. Temporary. Move later back to test
@Component
class BlogData(appConfig: AppConfig) {
    final val datetimeb1 = LocalDateTime.of(2024, 1,1,13,5,3)
    final val datetimeb2 = LocalDateTime.of(2024, 2,2,13,5,3)
    final val datetime1 = LocalDateTime.of(2024, 2,15,13,5,3)
    final val datetime2 = LocalDateTime.of(2024, 2,17,15,5,30)
    final val datetime3 = LocalDateTime.of(2024, 3,12,11,2,0)
    final val datetime23 = LocalDateTime.of(2024, 1,1,8,5,12)

    //    val Norwegian: LanguageCode = LanguageCode(NORWEGIAN, NB)
    //    val English: LanguageCode = LanguageCode(ENGLISH, EN)
    final val blogOwner: BlogOwner = BlogOwner(
        LocalDateTime.now(), null, appConfig.user, appConfig.password,
        "Hans Reier", "Sigmond", "reier.sigmond@gmail.com",
        "+4791668863", "Sløttvegen 17", "2390", "Moelv","NO"
    )
    private final val blogEntries1 = mutableListOf<BlogEntry>()
    private final val blogEntries1e = mutableListOf<BlogEntry>()
    private final val blogEntries2 = mutableListOf<BlogEntry>()
    private final val blog1 = Blog(datetime1, ENVIRONMENT, Norwegian, SUBJECT1, ABOUT1, blogEntries1, blogOwner)
    private final val blog1e = Blog(datetimeb1, ENVIRONMENT, English, SUBJECT1E, ABOUT1E, blogEntries1e, blogOwner)
    private final val blog2 = Blog(datetimeb2, ENERGY, Norwegian, SUBJECT2, ABOUT2, blogEntries2, blogOwner)

    private final val blogEntry1 = BlogEntry(datetime1, ELPOWER,  TITLE1, SUMMARY1, blog1)
    private final val blogEntry2 = BlogEntry(datetime2, WEATHER,  TITLE2, SUMMARY2, blog1)
    private final val blogEntry3 = BlogEntry(datetime3,  HIBERNATE,  TITLE3, SUMMARY3, blog1)
    private final val blogEntry1e = BlogEntry(datetime1,  ELPOWER,  TITLE1E, SUMMARY1E, blog1e)
    private final val blogEntry2e = BlogEntry(datetime2,  WEATHER,  TITLE2E, SUMMARY2E, blog1e)
    private final val blogEntry3e = BlogEntry(datetime3,  HIBERNATE,  TITLE3E, SUMMARY3E, blog1e)
    private final val blog2Entry3 = BlogEntry(datetime23,  ENERGY,  TITLE4, SUMMARY4, blog2)
    final val noOfBlogs: Int
    final val noOfBlogEntries: Int
    final val noOfBlog1Entries: Int

    init {
        val blogList = mutableSetOf<Blog>()
        blogOwner.blogs = blogList
        blogOwner.blogs?.add(blog1)
        blogOwner.blogs?.add(blog1e)
        blogOwner.blogs?.add(blog2)
        noOfBlogs = blogList.size

        blog1.blogEntries = blogEntries1
        blog1.blogEntries?.add(blogEntry1)
        blog1.blogEntries?.add(blogEntry2)
        blog1.blogEntries?.add(blogEntry3)
        noOfBlog1Entries = 3

        blog1e.blogEntries = blogEntries1e
        blog1e.blogEntries?.add(blogEntry1e)
        blog1e.blogEntries?.add(blogEntry2e)
        blog1e.blogEntries?.add(blogEntry3e)

        blog2.blogEntries = blogEntries2
        blog2.blogEntries?.add(blog2Entry3)

        noOfBlogEntries = blogList.sumOf { it.blogEntries?.size ?: 0 }
    }
}