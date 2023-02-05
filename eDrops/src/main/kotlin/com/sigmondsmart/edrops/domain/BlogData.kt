package com.sigmondsmart.edrops.domain

import java.time.LocalDateTime

const val TAG1 = "Sea"
const val TAG2 = "Mountain"
const val ENTRY1 = "Om strøm"
const val ENTRY2 = "Om været"
const val ENTRY3 = "Om Hibernate"
const val ENTRYNEW = "New blog"
const val ENTRYMOD = "Modified blog"
const val BLOG_TAG = "Environment"
const val SUBJECT1 = "Environmental issues"
const val SUBJECT2 = "Energy issues"
const val V1:Long  = 1
const val NORWEGIAN = "Norwegian"
const val NO = "no"

const val TEXT1 = """
    Regjeringen Støre nekter å regulere strømmarkedet.
    I stedet er det innført en strømstøtteordning.
    Denne er brukbar for privatpersoner, men ikke for bedrifter
    Bedriftene har muligheter til å inngå fastprisavtaler på et for høyt nivå.
    """
const val TEXT2 = """
    Først var det ikke snø,
    Så snødde det mye.
    Så kom det masse regn.
    Så ble det isglatt og iskaldt.
    """

const val TEXT3 = """
    Det er tydeligvis helt umulig med toveis relasjon for Hibernate på en til en relasjoner.
    Jeg har gitt opp å gjøre noe med det etter mange forsøk.
    """
const val NO_ENTRIES = 3
const val NO_ENTRIES_TOTAL = 4

// Initial populate table. Temporary. Move later back to test
class BlogData {
    val norwegian: LanguageCode = LanguageCode(NORWEGIAN, NO)
    val blogOwner: BlogOwner = BlogOwner(
        LocalDateTime.now(), null,
        "Hans Reier", "Sigmond", "reier.sigmond@gmail.com",
        "+4791668863", "Sløttvegen 17", "2390", "Moelv"
    )
    val blogEntries =  mutableListOf<BlogEntry>()
    private val blogEntries2 =  mutableListOf<BlogEntry>()
    val blog1 = Blog(LocalDateTime.now(), BLOG_TAG, norwegian, SUBJECT1, blogEntries, blogOwner)
    val blog2 = Blog(LocalDateTime.now(), BLOG_TAG, norwegian, SUBJECT2, blogEntries2, blogOwner)
    val blogEntry1 = BlogEntry(LocalDateTime.now(), LocalDateTime.now(), TAG1, V1, ENTRY1, blog1 )
    private val blogEntry2  = BlogEntry(LocalDateTime.now(), LocalDateTime.now(), TAG2, V1, ENTRY2, blog1)
    private val blogEntry3  = BlogEntry(LocalDateTime.now(), LocalDateTime.now(), TAG2, V1, ENTRY3, blog1)
    private val blog2Entry3 = BlogEntry(LocalDateTime.now(), LocalDateTime.now(), TAG2, V1, ENTRYNEW, blog2)

    init {

        val blogList = mutableSetOf<Blog>()
        blogOwner.blogs = blogList
        blogOwner.blogs?.add(blog1)
        blogOwner.blogs?.add(blog2)
        blog1.blogEntries  = blogEntries
        blog1.blogEntries?.add(blogEntry1)
        blog1.blogEntries?.add(blogEntry2)
        blog1.blogEntries?.add(blogEntry3)
        blog2.blogEntries = blogEntries2
        blog2.blogEntries?.add(blog2Entry3)
    }
}