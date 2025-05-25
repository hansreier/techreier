package com.techreier.edrops.data

import com.techreier.edrops.config.AppConfig
import com.techreier.edrops.data.blogs.climatenv.Climatenv
import com.techreier.edrops.data.blogs.coding.Coding
import com.techreier.edrops.data.blogs.energy.Energy
import com.techreier.edrops.data.blogs.politics.Politics
import com.techreier.edrops.domain.Blog
import com.techreier.edrops.domain.BlogOwner
import com.techreier.edrops.util.timestamp

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
    private val blogOwnerCreated = timestamp("08.01.1963 12:00:00")

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
        blogOwner.blogs = blogList
        blogOwner.blogs.add(Politics.no(blogOwner, base.politicsNo))
        blogOwner.blogs.add(Politics.en(blogOwner, base.politicsEn))
        blogOwner.blogs.add(Energy.no(blogOwner, base.energyNo))
        blogOwner.blogs.add(Energy.en(blogOwner, base.energyEn))
        blogOwner.blogs.add(Coding.no(blogOwner, base.codingNo))
        blogOwner.blogs.add(Coding.en(blogOwner, base.codingEn))
        blogOwner.blogs.add(Climatenv.no(blogOwner, base.energyNo))
        blogOwner.blogs.add(Climatenv.en(blogOwner, base.energyEn))
    }
}