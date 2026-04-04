package com.techreier.edrops.repository

import com.techreier.edrops.domain.Blog
import com.techreier.edrops.dto.MenuItem
import com.techreier.edrops.repository.projections.IBlog
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

    @Query("""
    SELECT b.id as id,
           b.changed as changed,
           b.segment as segment,
           b.subject as subject,
           b.about as about,
           b.pos as pos,
           b.topic.topicKey as topicKey,
           b.topic.text as topicText,
           b.topic.language.code as languageCode 
    FROM Blog b 
    WHERE b.id = :id
""")
    fun findPById(id:Long): IBlog?

    // Used in data initialization and tests
    @EntityGraph(attributePaths = ["blogOwner", "topic", "topic.language"])
    fun findByTopicLanguageCodeAndSegment(languageCode: String, segment: String): List<Blog>

    @Query( """
    SELECT new com.techreier.edrops.dto.MenuItem(b.topic.language.code, b.segment,  b.topic.topicKey, b.subject, false) 
    FROM Blog b 
    WHERE b.topic.language.code = :languageCode
    AND b.pos >= :minPos
    ORDER BY b.topic.pos, b.pos
    """)
    fun getMenuItems(languageCode: String, minPos: Int): List<MenuItem>

    @Query("""
    SELECT b.id FROM Blog b 
    WHERE b.segment = :segment AND b.topic.language.code = :lang
    AND b.pos >= :minPos
    """)
    fun findIdBySegmentAndTopicLanguageCode(segment: String, lang: String, minPos: Int): List<Long>

    @Query("""
    SELECT b.id FROM Blog b 
    WHERE b.segment = :segment 
    AND b.blogOwner.id = :blogOwnerId 
    AND b.topic.language.code = :languageCode
    ORDER BY b.changed DESC, b.id DESC
""")
    fun findBlogIds(segment: String, blogOwnerId: Long, languageCode: String): List<Long>

}
