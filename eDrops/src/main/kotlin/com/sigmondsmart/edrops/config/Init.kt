package com.sigmondsmart.edrops.config

import com.sigmondsmart.edrops.domain.BlogData
import com.sigmondsmart.edrops.domain.English
import com.sigmondsmart.edrops.domain.Norwegian
import com.sigmondsmart.edrops.repository.BlogOwnerRepository
import com.sigmondsmart.edrops.repository.LanguageRepository
import org.springframework.context.annotation.Configuration

@Configuration
class Init(
    languageRepo: LanguageRepository,
    ownerRepo: BlogOwnerRepository
) {
    init {
        if (ownerRepo.count() == 0L) {
            val blogData = BlogData()
            languageRepo.save(Norwegian)
            languageRepo.save(English)
            ownerRepo.save(blogData.blogOwner)
            logger.info("Initialized with data")
        } else {
            logger.info("Initial data was already there, skipping")
        }
    }
}