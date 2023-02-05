package com.sigmondsmart.edrops.repository

import com.sigmondsmart.edrops.domain.Blog
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface BlogRepository : JpaRepository<Blog, Long> {
 //add hoc entity graph https://www.baeldung.com/spring-data-jpa-named-entity-graphs
 @EntityGraph(attributePaths = ["language", "blogOwner","blogEntries"])
 override fun findAll(): MutableList<Blog>

// @EntityGraph(attributePaths = ["language", "blogOwner", "blogEntries"])
 //@EntityGraph(attributePaths = ["language",  "blogEntries"])
 @EntityGraph(attributePaths = ["blogOwner", "language","blogEntries"])
 override fun findById(id: Long): Optional<Blog>

}