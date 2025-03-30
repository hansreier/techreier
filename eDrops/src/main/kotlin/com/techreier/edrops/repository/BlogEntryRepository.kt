package com.techreier.edrops.repository

import com.techreier.edrops.domain.BlogEntry
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BlogEntryRepository : JpaRepository<BlogEntry, Long> {

  fun findByTitle(text: String): List<BlogEntry>

}