package com.sigmondsmart.edrops.repository

import com.sigmondsmart.edrops.domain.Blog
import com.sigmondsmart.edrops.domain.LanguageCode
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface BlogRepository : JpaRepository<Blog, Long> {
 //add hoc entity graph https://www.baeldung.com/spring-data-jpa-named-entity-graphs
 @EntityGraph(attributePaths = ["blogOwner", "language","blogEntries"])
 override fun findAll(): MutableList<Blog>

 @EntityGraph(attributePaths = ["blogOwner", "language"])
 override fun findById(id: Long): Optional<Blog>

 @EntityGraph(attributePaths = ["blogOwner", "language"])
 fun findByLanguage(language: LanguageCode): MutableSet<Blog>

 @EntityGraph(attributePaths = ["blogOwner", "language"])
 fun findFirstByLanguageAndTag(language: LanguageCode, tag: String):Blog?

}