package com.sigmondsmart.edrops.domain

import java.time.LocalDateTime

const val TAG1 = "Sea"
const val TAG2 = "Mountain"
const val ENTRY1 = "My first blog"
const val ENTRY2 = "My next blog"
const val ENTRY3 = "New blog"
const val ENTRYMOD = "Modified blog"
const val BLOG_TAG = "Environment"
const val SUBJECT = "Environmental issues"
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
    val blog1 = Blog(LocalDateTime.now(), BLOG_TAG, norwegian, SUBJECT, blogEntries, blogOwner)
    private val blog2 = Blog(LocalDateTime.now(), BLOG_TAG, norwegian, SUBJECT, blogEntries2, blogOwner)
    val blogEntry = BlogEntry(LocalDateTime.now(), LocalDateTime.now(), TAG1, V1, ENTRY1, blog1 )
    private val blogText1 = blogEntry.id?.let { BlogText(TEXT1, blogEntry, it) }
    private val blogEntry2  = BlogEntry(LocalDateTime.now(), LocalDateTime.now(), TAG2, V1, ENTRY2, blog1)
    private val blogText2 = blogEntry.id?.let { BlogText(TEXT2, blogEntry2, it) }
    private val blogEntry3 = BlogEntry(LocalDateTime.now(), LocalDateTime.now(), TAG2, V1, ENTRY3, blog2)

    init {
        val blogList = mutableSetOf<Blog>()
        blogOwner.blogs = blogList
        blogOwner.blogs?.add(blog1)
        blogOwner.blogs?.add(blog2)
        blog1.blogEntries  = blogEntries
        blog1.blogEntries?.add(blogEntry)
        blog1.blogEntries?.add(blogEntry2)
        blog2.blogEntries = blogEntries2
        blog2.blogEntries?.add(blogEntry3)
        blogEntry.blogText = blogText1
        blogEntry2.blogText = blogText2
    }
}