package com.techreier.edrops.repository

import com.techreier.edrops.domain.BlogPost
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BlogPostRepository : JpaRepository<BlogPost, Long> {

  fun findByTitle(text: String): List<BlogPost>

}