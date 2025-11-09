package com.techreier.edrops.data

import com.techreier.edrops.config.AppConfig
import com.techreier.edrops.data.blogs.climatenv.Climatenv
import com.techreier.edrops.data.blogs.coding.Coding
import com.techreier.edrops.data.blogs.energy.Elpower
import com.techreier.edrops.data.blogs.politics.Politics
import com.techreier.edrops.domain.Blog
import com.techreier.edrops.domain.BlogOwner
import com.techreier.edrops.util.checkDuplicates
import java.time.Instant

private const val OWNER_FIRSTNAME = "Hans Reier"
private const val OWNER_LASTNAME = "Sigmond"
private const val OWNER_EMAIL = "reier.sigmond@gmail.com"
private const val OWNER_PHONE="+4791668863"
private const val OWNER_ADDRESS="Sløttvegen 17"
private const val OWNER_ZIP="3290"
private const val OWNER_LOCATION="Moelv"

class Initial(
    appConfig: AppConfig,
    val base: Base,
) {
    private val blogOwnerCreated = Instant.now()

    private val blogList = mutableSetOf<Blog>()

    val blogOwner: BlogOwner =
        BlogOwner(
            blogOwnerCreated, null, appConfig.user, appConfig.password,
            OWNER_FIRSTNAME, OWNER_LASTNAME, OWNER_EMAIL, OWNER_PHONE, OWNER_ADDRESS, OWNER_ZIP, OWNER_LOCATION, NB,
            blogList
        )

    init {
        initialize()
    }

    private fun initialize() {
        blogList.clear()
        blogList.add(Politics.no(blogOwner, base.politicsNo))
        blogList.add(Politics.en(blogOwner, base.politicsEn))
        blogList.add(Elpower.no(blogOwner, base.energyNo))
        blogList.add(Elpower.en(blogOwner, base.energyEn))
        blogList.add(Coding.no(blogOwner, base.codingNo))
        blogList.add(Coding.en(blogOwner, base.codingEn))
        blogList.add(Climatenv.no(blogOwner, base.energyNo))
        blogList.add(Climatenv.en(blogOwner, base.energyEn))
        blogList.checkDuplicates  { it.segment to it.topic.language.code}
        blogList.forEach { blog -> blog.blogPosts.checkDuplicates { it.segment } }
        blogOwner.blogs = blogList
    }
}