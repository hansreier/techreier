package com.techreier.edrops.forms

import java.time.ZonedDateTime

data class BlogEntryForm(var id: Long?, var segment: String = "", var title: String = "", var summary: String = "",
                         var changed: ZonedDateTime?)