package com.techreier.edrops.config

import com.techreier.edrops.domain.BlogData
import com.techreier.edrops.domain.English
import com.techreier.edrops.domain.Norwegian
import com.techreier.edrops.repository.BlogOwnerRepository
import com.techreier.edrops.repository.LanguageRepository
import com.techreier.edrops.repository.TopicRepository
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("!test")
class Init(
    languageRepo: LanguageRepository,
    ownerRepo: BlogOwnerRepository,
    topicRepo: TopicRepository,
    appConfig: AppConfig,
) {
    init {
        logger.info("App name: ${appConfig.appname}")
        if (ownerRepo.count() == 0L) {
            val blogData = BlogData(appConfig)
            languageRepo.save(Norwegian)
            languageRepo.save(English)
            topicRepo.save(blogData.defaultNo)
            topicRepo.save(blogData.defaultEn)
            ownerRepo.save(blogData.blogOwner) //TODO Does not work is any of the Topics contains relation to blogOwner.
            logger.info("Initialized with data")
        } else {
            logger.info("Initial data was already there, skipping")
        }
    }
}