package com.techreier.edrops.config

import com.techreier.edrops.domain.BlogData
import com.techreier.edrops.domain.BlogOwner
import com.techreier.edrops.domain.Base
import com.techreier.edrops.repository.BlogOwnerRepository
import com.techreier.edrops.repository.LanguageRepository
import com.techreier.edrops.repository.TopicRepository
import com.techreier.edrops.util.buildVersion
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

lateinit var  blogAdmin:BlogOwner

@Configuration
@Profile("!test")
class Init(
    languageRepo: LanguageRepository,
    ownerRepo: BlogOwnerRepository,
    topicRepo: TopicRepository,
    appConfig: AppConfig,
) {
    init {
        val buildVersion = buildVersion(appConfig.buildTime, false)
        logger.info("App name: ${appConfig.appname} built: ${buildVersion}")
        if (!appConfig.auth) {
            logger.warn("Admin user auth is off, turn on and redeploy if production")
        }

        if (ownerRepo.count() == 0L) {
            val base = Base()
            val blogData = BlogData(appConfig, base)
            languageRepo.saveAll(base.languages)
            topicRepo.saveAll(base.topics)
            val blogOwner = ownerRepo.save(blogData.blogOwner)
            blogAdmin = blogOwner
            logger.info("Initialized with data")
        } else {
            logger.info("Initial data was already there, skipping")
        }
    }
}
