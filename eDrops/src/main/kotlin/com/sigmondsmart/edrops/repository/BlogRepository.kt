package com.sigmondsmart.edrops.repository

import com.sigmondsmart.edrops.domain.Blog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BlogRepository : JpaRepository<Blog, Long> {
}