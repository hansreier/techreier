package com.sigmondsmart.edrops.repository

import com.sigmondsmart.edrops.domain.BlogEntry
import com.sigmondsmart.edrops.domain.BlogOwner
import java.time.LocalDateTime

const val FIRST_ENTRY = "My first blog"
const val SECOND_ENTRY = "My next blog"
const val MODIFIED_ENTRY = "Modified blog"

class BlogData {
    val blogOwner: BlogOwner = BlogOwner(
        LocalDateTime.now(), null,
        "Hans Reier", "Sigmond", "reier.sigmond@gmail.com",
        "+4791668863", "Sløttvegen 17", "2390", "Moelv"
    )
    val blogEntry: BlogEntry = BlogEntry(LocalDateTime.now(), LocalDateTime.now(), FIRST_ENTRY, blogOwner)
    val blogEntry2: BlogEntry = BlogEntry(LocalDateTime.now(), LocalDateTime.now(), SECOND_ENTRY, blogOwner)
}