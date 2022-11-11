package com.sigmondsmart.edrops.repository

import com.sigmondsmart.edrops.domain.BlogOwner
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BlogOwnerRepository : JpaRepository<BlogOwner, Long> {
}