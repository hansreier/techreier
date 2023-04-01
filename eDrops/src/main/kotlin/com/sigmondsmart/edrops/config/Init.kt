package com.sigmondsmart.edrops.config

import com.sigmondsmart.edrops.domain.BlogData
import com.sigmondsmart.edrops.repository.BlogOwnerRepository
import com.sigmondsmart.edrops.repository.LanguageRepository
import org.springframework.context.annotation.Configuration

@Configuration
class Init(languageRepo: LanguageRepository,
           ownerRepo: BlogOwnerRepository
) {
    init {
        logger.info("Reier was here mama")
        val blogData = BlogData()
        languageRepo.save(blogData.norwegian)
        languageRepo.save(blogData.english)
        ownerRepo.save(blogData.blogOwner)
        logger.info("Initialized with data")
    }
}