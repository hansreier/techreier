package com.sigmondsmart.edrops.repository

import com.sigmondsmart.edrops.domain.Blog
import com.sigmondsmart.edrops.domain.LanguageCode
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

// Entitygraph is static and not flexible
//https://stackoverflow.com/questions/33957291/how-to-fetch-entitygraph-dynamically-in-spring-boot
@Repository
interface BlogRepository : JpaRepository<Blog, Long> {
    //add hoc entity graph https://www.baeldung.com/spring-data-jpa-named-entity-graphs
    @EntityGraph(attributePaths = ["blogOwner", "language", "blogEntries"])
    override fun findAll(): MutableList<Blog>

    @EntityGraph(attributePaths = ["blogOwner", "language"])
    override fun findById(id: Long): Optional<Blog>

    @EntityGraph(attributePaths = ["blogOwner", "language", "blogEntries"])
    fun findAllById(id: Long): Optional<Blog>

    @EntityGraph(attributePaths = ["blogOwner", "language", "blogEntries"])
    fun findAllById(pageRequest: PageRequest, id: Long): Optional<Blog>

    @EntityGraph(attributePaths = ["blogOwner", "language"])
    fun findByLanguage(language: LanguageCode): MutableSet<Blog>

    //Works, but cannot include blogEntries in entityGraph (when only first blogEntry is selected)
    @EntityGraph(attributePaths = ["blogOwner", "language"])
    fun findFirstBlogByLanguageAndTag(language: LanguageCode, tag: String): Blog?

   // @EntityGraph(attributePaths = ["blogOwner", "language"])
    //@Query("SELECT b FROM Blog b WHERE b.language=:l AND b.tag=:t")
    //fun findFirstBlog2ByLanguageAndTag(@Param("l") l: LanguageCode, @Param("t") tag: String): MutableSet<Blog>?

}