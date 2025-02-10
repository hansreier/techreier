package com.techreier.edrops.repository

import com.techreier.edrops.domain.Blog
import com.techreier.edrops.dto.MenuItemDTO
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

// Entitygraph is static and not flexible
//https://stackoverflow.com/questions/33957291/how-to-fetch-entitygraph-dynamically-in-spring-boot
@Repository
interface BlogRepository : JpaRepository<Blog, Long> {
    //add hoc entity graph https://www.baeldung.com/spring-data-jpa-named-entity-graphs
    @EntityGraph(attributePaths = ["blogOwner", "topic","topic.language", "blogEntries"])
    override fun findAll(): MutableList<Blog>

    @EntityGraph(attributePaths = ["blogOwner", "topic", "topic.language"])
    override fun findById(id: Long): Optional<Blog>

    @EntityGraph(attributePaths = ["blogOwner", "topic", "topic.language", "blogEntries"])
    fun findAllById(id: Long): Optional<Blog>

    @EntityGraph(attributePaths = ["blogOwner", "topic", "topic.language"])
    fun findByTopicLanguageCode(languageCode: String): MutableSet<Blog>

    @EntityGraph(attributePaths = ["blogOwner", "topic", "topic.language", "blogEntries"])
    fun findFirstBlogByTopicLanguageCodeAndSegment(languageCode: String, segment: String): Blog?

    @Query("SELECT new com.techreier.edrops.dto.MenuItemDTO(b.id, b.subject, b.segment) " +
            " FROM Blog b where b.topic.language.code = :languageCode ORDER BY b.menuOrder")

    fun getMenuItems(languageCode: String): List<MenuItemDTO>

}