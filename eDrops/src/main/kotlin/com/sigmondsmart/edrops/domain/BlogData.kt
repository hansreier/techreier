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
    val blog: Blog = Blog(LocalDateTime.now(), BLOG_TAG, norwegian, SUBJECT, blogEntries, blogOwner)
    private val blog2: Blog = Blog(LocalDateTime.now(), BLOG_TAG, norwegian, SUBJECT, blogEntries2, blogOwner)
    val blogEntry: BlogEntry = BlogEntry(LocalDateTime.now(), LocalDateTime.now(), TAG1, V1, ENTRY1, blog)
    private val blogEntry2: BlogEntry = BlogEntry(LocalDateTime.now(), LocalDateTime.now(), TAG2, V1, ENTRY2, blog)
    private val blogEntry3: BlogEntry = BlogEntry(LocalDateTime.now(), LocalDateTime.now(), TAG2, V1, ENTRY3, blog2)

    init {
        val blogList = mutableListOf<Blog>()
        blogOwner.blogs = blogList
        blogOwner.blogs?.add(blog)
        blogOwner.blogs?.add(blog2)
        blog.blogEntries  = blogEntries
        blog.blogEntries?.add(blogEntry)
        blog.blogEntries?.add(blogEntry2)
        blog2.blogEntries = blogEntries2
        blog2.blogEntries?.add(blogEntry3)
    }
}