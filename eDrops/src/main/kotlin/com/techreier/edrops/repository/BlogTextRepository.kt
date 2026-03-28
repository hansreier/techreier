package com.techreier.edrops.repository

import com.techreier.edrops.domain.BlogText
import com.techreier.edrops.repository.projections.IBlogText
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BlogTextRepository : JpaRepository<BlogText, Long> {
    fun findPById(id: Long): IBlogText?
}