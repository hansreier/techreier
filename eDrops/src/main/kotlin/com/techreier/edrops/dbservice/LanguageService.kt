package com.techreier.edrops.dbservice

import com.techreier.edrops.domain.LanguageCode
import com.techreier.edrops.repository.LanguageRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class LanguageService(
    private val languageRepo: LanguageRepository,
) {
    fun saveAll(languages: List<LanguageCode>) {
        languageRepo.saveAll(languages)
    }

}