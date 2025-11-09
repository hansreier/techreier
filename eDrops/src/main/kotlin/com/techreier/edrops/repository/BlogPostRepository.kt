package com.techreier.edrops.repository

import com.techreier.edrops.domain.Blog
import com.techreier.edrops.domain.BlogPost
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface BlogPostRepository : JpaRepository<BlogPost, Long> {

  fun findByTitle(text: String): List<BlogPost>

  // TODO functions are really identical
  fun findByBlogAndSegment(blog: Blog, segment: String): BlogPost?

  @Query("SELECT b.id FROM BlogPost b WHERE b.segment = :segment AND b.blog.id = :blogId")
  fun findBlogPostIds(segment: String, blogId: Long): List<Long>

}