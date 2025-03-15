package com.techreier.edrops.repository

import com.techreier.edrops.domain.Blog
import com.techreier.edrops.dto.BlogLanguageDTO
import com.techreier.edrops.dto.MenuItemDTO
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

// Entitygraph is static and not flexible
// https://stackoverflow.com/questions/33957291/how-to-fetch-entitygraph-dynamically-in-spring-boot
@Repository
interface BlogRepository : JpaRepository<Blog, Long> {
    // add hoc entity graph https://www.baeldung.com/spring-data-jpa-named-entity-graphs
    @EntityGraph(attributePaths = ["blogOwner", "topic", "topic.language", "blogEntries"])
    override fun findAll(): MutableList<Blog>

    @EntityGraph(attributePaths = ["blogOwner", "topic", "topic.language"])
    override fun findById(id: Long): Optional<Blog>

    @EntityGraph(attributePaths = ["blogOwner", "topic", "topic.language", "blogEntries"])
    fun findWithEntriesById(id: Long): Optional<Blog>

    @EntityGraph(attributePaths = ["blogOwner", "topic", "topic.language"])
    fun findByTopicLanguageCode(languageCode: String): MutableSet<Blog>

    // TODO Not really used any more. Consider removing.
    @EntityGraph(attributePaths = ["blogOwner", "topic", "topic.language", "blogEntries"])
    fun findWithEntriesByTopicLanguageCodeAndSegment(
        languageCode: String,
        segment: String,
    ): Blog?

    // TODO Not really used any more except in test. Consider removing.
    @EntityGraph(attributePaths = ["blogOwner", "topic", "topic.language"])
    fun findByTopicLanguageCodeAndSegment(
        languageCode: String,
        segment: String,
    ): Blog?

    //TODO evaluate later if all attributes really is needed
    @Query(
        "SELECT new com.techreier.edrops.dto.MenuItemDTO(b.topic.language.code, b.subject, b.segment, b.topic.topicKey) " +
            " FROM Blog b WHERE b.topic.language.code = :languageCode ORDER BY b.topic.pos, b.pos",
    )
    fun getMenuItems(
        languageCode: String
    ): List<MenuItemDTO>

    // Assumption: Only one owner. TODO Check for not saving blogs that returns duplicates here. Will crash here.
    @Query(
        "SELECT new com.techreier.edrops.dto.BlogLanguageDTO(b.id, b.topic.language.code) " +
            " FROM Blog b WHERE b.segment = :segment AND b.topic.language.code = :languageCode",
    )
    fun getBlogWithLanguageCode(
        segment: String,
        languageCode: String,
    ): BlogLanguageDTO?
}
