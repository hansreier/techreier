package com.techreier.edrops.config

import com.techreier.edrops.domain.BlogData
import com.techreier.edrops.domain.English
import com.techreier.edrops.domain.Norwegian
import com.techreier.edrops.repository.BlogOwnerRepository
import com.techreier.edrops.repository.LanguageRepository
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
class Init(
    languageRepo: LanguageRepository,
    ownerRepo: BlogOwnerRepository,
    appConfig: AppConfig,
    passwordEncoder: PasswordEncoder
) {
    init {
        logger.info("App name: ${appConfig.appname}")
        if (ownerRepo.count() == 0L) {
            val blogData = BlogData(passwordEncoder)
            languageRepo.save(Norwegian)
            languageRepo.save(English)
            ownerRepo.save(blogData.blogOwner)
            logger.info("Initialized with data")
        } else {
            logger.info("Initial data was already there, skipping")
        }
    }
}