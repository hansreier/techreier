package com.techreier.edrops.repository

import com.techreier.edrops.domain.BlogPost
import com.techreier.edrops.repository.projections.IBlogPostSummary
import com.techreier.edrops.repository.projections.IBlogPost
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface BlogPostRepository : JpaRepository<BlogPost, Long> {

    fun findByTitle(text: String, state: String): List<BlogPost>

    fun findByBlogIdAndSegmentAndState(blogId: Long, segment: String, state: String): List<BlogPost>

    fun findByBlogId(blogId: Long, sort: Sort): List<IBlogPost>

    // Problem, sorts by only one field
    fun findByBlogIdAndState(blogId: Long, state: String, sort: Sort): List<IBlogPost>

    @Query("""
    SELECT b FROM BlogPost b 
    WHERE b.blog.id = :blogId 
    AND b.state = :state 
    ORDER BY COALESCE(b.bumped, b.changed) DESC, b.id DESC
""")
    fun findByBlogIdAndStateSorted(
        @Param("blogId") blogId: Long,
        @Param("state") state: String
    ): List<IBlogPost>

    fun findPByBlogIdAndSegmentAndState(blogId: Long, segment: String, state: String): List<IBlogPost>

    @Query(
        """
    SELECT b.id AS id, COALESCE(b.bumped, b.changed) AS bumped FROM BlogPost b 
    WHERE b.segment = :segment 
    AND b.blog.id = :blogId 
    AND b.state = :state 
    ORDER BY COALESCE(b.bumped, b.changed) DESC, b.id DESC
"""
    )
    fun findBlogPostSummaries(segment: String, blogId: Long, state: String): List<IBlogPostSummary>
}