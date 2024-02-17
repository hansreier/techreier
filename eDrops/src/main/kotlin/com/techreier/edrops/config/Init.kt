package com.techreier.edrops.config

import com.techreier.edrops.domain.BlogData
import com.techreier.edrops.domain.English
import com.techreier.edrops.domain.Norwegian
import com.techreier.edrops.repository.BlogOwnerRepository
import com.techreier.edrops.repository.LanguageRepository
import org.springframework.context.annotation.Configuration

@Configuration
class Init(
    languageRepo: LanguageRepository,
    ownerRepo: BlogOwnerRepository,
    appConfig: AppConfig,
    blogData: BlogData
) {
    init {
        logger.info("App name: ${appConfig.appname}")
        if (ownerRepo.count() == 0L) {
            languageRepo.save(Norwegian)
            languageRepo.save(English)
            ownerRepo.save(blogData.blogOwner)
            logger.info("Initialized with data")
        } else {
            logger.info("Initial data was already there, skipping")
        }
    }
}