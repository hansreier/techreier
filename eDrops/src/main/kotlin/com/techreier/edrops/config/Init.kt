package com.techreier.edrops.config

import com.techreier.edrops.domain.BlogData
import com.techreier.edrops.domain.Common
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
            val common = Common()
            val blogData = BlogData(appConfig, common)
            languageRepo.saveAll(common.languages)
            topicRepo.saveAll(common.topics)
            ownerRepo.save(blogData.blogOwner)
            logger.info("Initialized with data")
        } else {
            logger.info("Initial data was already there, skipping")
        }
    }
}
