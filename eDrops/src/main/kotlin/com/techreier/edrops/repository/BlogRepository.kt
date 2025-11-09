package com.techreier.edrops.repository

import com.techreier.edrops.domain.Blog
import com.techreier.edrops.dto.BlogLanguageDTO
import com.techreier.edrops.dto.MenuItem
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
    @EntityGraph(attributePaths = ["blogOwner", "topic", "topic.language", "blogPosts"])
    override fun findAll(): MutableList<Blog>

    @EntityGraph(attributePaths = ["blogOwner", "topic", "topic.language"])
    override fun findById(id: Long): Optional<Blog>

    @EntityGraph(attributePaths = ["blogOwner", "topic", "topic.language", "blogPosts"])
    fun findWithPostsById(id: Long): Optional<Blog>

    @EntityGraph(attributePaths = ["blogOwner", "topic", "topic.language"])
    fun findByTopicLanguageCode(languageCode: String): MutableSet<Blog>

    @EntityGraph(attributePaths = ["blogOwner", "topic", "topic.language"])
    fun findByTopicLanguageCodeAndSegment(languageCode: String, segment: String): List<Blog>

    @Query(
        "SELECT new com.techreier.edrops.dto.MenuItem(b.topic.language.code, b.segment,  b.topic.topicKey, b.subject, false) " +
                " FROM Blog b WHERE b.topic.language.code = :languageCode ORDER BY b.topic.pos, b.pos",
    )
    fun getMenuItems(languageCode: String): List<MenuItem>

    // Assumption: Only one owner. TODO Check for not saving blogs that returns duplicates here. Will crash here.
    @Query(
        "SELECT new com.techreier.edrops.dto.BlogLanguageDTO(b.id, b.topic.language.code) " +
                " FROM Blog b WHERE b.segment = :segment AND b.topic.language.code = :languageCode",
    )
    fun getBlogWithLanguageCode(segment: String, languageCode: String): BlogLanguageDTO?

    @Query(
        "SELECT b.id FROM Blog b " +
                "WHERE b.segment = :segment AND b.blogOwner.id = :blogOwnerId AND b.topic.language.code = :languageCode"
    )
    fun findBlogIds(segment: String, blogOwnerId: Long, languageCode: String): List<Long>
}
