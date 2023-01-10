package com.sigmondsmart.edrops.repository

import com.sigmondsmart.edrops.domain.BlogEntry
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BlogEntryRepository : JpaRepository<BlogEntry, Long> {
  //  fun getBlogEntry(id: Long): BlogEntry?
  fun findByTitle(text: String): Iterable<BlogEntry>
}