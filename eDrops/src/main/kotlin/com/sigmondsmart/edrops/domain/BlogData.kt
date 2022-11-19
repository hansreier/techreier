package com.sigmondsmart.edrops.domain

import java.time.LocalDateTime

const val FIRST_ENTRY = "My first blog"
const val SECOND_ENTRY = "My next blog"
const val MODIFIED_ENTRY = "Modified blog"

// Initial populate table. Temporary. Move later back to test
class BlogData {
    val blogOwner: BlogOwner = BlogOwner(
        LocalDateTime.now(), null,
        "Hans Reier", "Sigmond", "reier.sigmond@gmail.com",
        "+4791668863", "Sløttvegen 17", "2390", "Moelv"
    )
    val blogEntries =  mutableListOf<BlogEntry>()
    val blog: Blog = Blog(LocalDateTime.now(), blogEntries, blogOwner)
    val blogEntry: BlogEntry = BlogEntry(LocalDateTime.now(), LocalDateTime.now(), FIRST_ENTRY, blog)
    val blogEntry2: BlogEntry = BlogEntry(LocalDateTime.now(), LocalDateTime.now(), SECOND_ENTRY, blog)

    init {
        val blogList = mutableListOf<Blog>()
        blogOwner.blogs = blogList
        blogOwner.blogs?.add(blog)
        val blogEntryList = mutableListOf<BlogEntry>()
        blog.blogEntries  = blogEntryList
        blog.blogEntries?.add(blogEntry)
        blog.blogEntries?.add(blogEntry2)
    }
}