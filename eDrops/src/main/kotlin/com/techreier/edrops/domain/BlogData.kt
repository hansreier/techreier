package com.techreier.edrops.domain

import com.techreier.edrops.config.AppConfig
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.time.LocalDateTime

const val SEGMENT1 = "Sjoe"
const val SEGMENT1E = "Sea"
const val SEGMENT2 = "Fjell"
const val SEGMENT2E = "Montain"

const val TITLE1 = "Om strøm"
const val TITLE1E = "About electric power"
const val TITLE2 = "Om været"
const val TITLE2E = "About weather"
const val TITLE3 = "Om Hibernate"
const val TITLE3E = "About Hibernate"
const val TITLE4 = "Energi blog"
const val TITLE4MOD = "Modified blog"
const val ENV_SEGMENT = "env"
const val ENERGY_SEGMENT = "energy"
const val SUBJECT1 = "Miljø saker"
const val SUBJECT1E = "Environmental issues"
const val SUBJECT2 = "Energi saker"
const val V1: Long = 1
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
    Denne er brukbar for privatpersoner, men ikke for bedrifter
    Bedriftene har muligheter til å inngå fastprisavtaler på et for høyt nivå.
    """
const val SUMMARY1E = """
    The gouvernment Støre refuses to control the electricity marked.
    As a replacement you get some money returned back from Norwegian Authorities.
    It is not that bad for private citizens, but not for companies
    The companies can chose fixed price deals at a too high price level
    """
const val SUMMARY2 = """
    Først var det ikke snø,
    Så snødde det mye.
    Så kom det masse regn.
    Så ble det isglatt og iskaldt.
    """

const val SUMMARY2E = """
    At first no snow
    Then it snowed a lot,
    Then a lot of rain poured down.
    Then it god icy and slippery and freezing cold.
    """

const val SUMMARY3 = """
    Det er tydeligvis helt umulig med toveis relasjon for Hibernate på en til en relasjoner.
    Jeg har gitt opp å gjøre noe med det etter mange forsøk.
    """
const val SUMMARY3E = """
    It is apparently impossible with a two way relation for Hibernate on a one to one relation.
    I have given up doing something about it after many attempts.
    """

const val SUMMARY4 = """
    Dette er min nye blogg med tvilsomt innhold.
    """

// Initial populate table. Temporary. Move later back to test
@Component
class BlogData(passwordEncoder: PasswordEncoder, appConfig: AppConfig) {

    //    val Norwegian: LanguageCode = LanguageCode(NORWEGIAN, NB)
    //    val English: LanguageCode = LanguageCode(ENGLISH, EN)
    final val blogOwner: BlogOwner = BlogOwner(
        LocalDateTime.now(), null, appConfig.user,
        passwordEncoder.encode(appConfig.password),
        "Hans Reier", "Sigmond", "reier.sigmond@gmail.com",
        "+4791668863", "Sløttvegen 17", "2390", "Moelv"
    )
    final val blogEntries1 = mutableListOf<BlogEntry>()
    private final val blogEntries1e = mutableListOf<BlogEntry>()
    private final val blogEntries2 = mutableListOf<BlogEntry>()
    private final val timestamp: LocalDateTime = LocalDateTime.now()
    final val blog1 = Blog(timestamp, ENV_SEGMENT, Norwegian, SUBJECT1, blogEntries1, blogOwner)
    private final val blog1e = Blog(timestamp, ENV_SEGMENT, English, SUBJECT1E, blogEntries1e, blogOwner)
    private final val blog2 = Blog(timestamp, ENERGY_SEGMENT, Norwegian, SUBJECT2, blogEntries2, blogOwner)

    final val blogEntry1 = BlogEntry(timestamp, timestamp, SEGMENT1, V1, TITLE1, SUMMARY1, blog1)
    private final val blogEntry2 = BlogEntry(timestamp, timestamp, SEGMENT2, V1, TITLE2, SUMMARY2, blog1)
    private final val blogEntry3 = BlogEntry(timestamp, timestamp, SEGMENT2, V1, TITLE3, SUMMARY3, blog1)
    private final val blogEntry1e = BlogEntry(timestamp, timestamp, SEGMENT1E, V1, TITLE1E, SUMMARY1E, blog1e)
    private final val blogEntry2e = BlogEntry(timestamp, timestamp, SEGMENT2E, V1, TITLE2E, SUMMARY2E, blog1e)
    private final val blogEntry3e = BlogEntry(timestamp, timestamp, SEGMENT2E, V1, TITLE3E, SUMMARY3E, blog1e)
    private final val blog2Entry3 = BlogEntry(timestamp, timestamp, SEGMENT2, V1, TITLE4, SUMMARY4, blog2)
    final val noOfBlogs: Int
    final val noOfBlogEntries: Int

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

        blog1e.blogEntries = blogEntries1e
        blog1e.blogEntries?.add(blogEntry1e)
        blog1e.blogEntries?.add(blogEntry2e)
        blog1e.blogEntries?.add(blogEntry3e)

        blog2.blogEntries = blogEntries2
        blog2.blogEntries?.add(blog2Entry3)

        noOfBlogEntries = blogList.sumOf { it.blogEntries?.size ?: 0 }
    }
}