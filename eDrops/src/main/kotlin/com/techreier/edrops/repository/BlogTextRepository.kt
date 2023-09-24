package com.techreier.edrops.repository

import com.techreier.edrops.domain.BlogText
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BlogTextRepository : JpaRepository<BlogText, Long> {
}