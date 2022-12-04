package com.sigmondsmart.edrops.repository

import com.sigmondsmart.edrops.domain.LanguageCode
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface LanguageRepository : JpaRepository<LanguageCode, String> {
}