package com.techreier.edrops.repository

import com.techreier.edrops.domain.BlogPost
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface BlogPostRepository : JpaRepository<BlogPost, Long> {

  fun findByTitle(text: String, state: String): List<BlogPost>

  fun findByBlogIdAndSegment(blogId: Long, segment: String): List<BlogPost>

  fun findByBlogIdAndSegment(blogId: Long, segment: String, state: String): List<BlogPost>

  @Query("SELECT b.id FROM BlogPost b WHERE b.segment = :segment " +
          "AND b.blog.id = :blogId " +
          "AND b.state = :state")
  fun findBlogPostIds(segment: String, blogId: Long, state: String): List<Long>

}