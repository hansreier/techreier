package com.techreier.edrops.repository

import com.techreier.edrops.domain.BlogOwner
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface BlogOwnerRepository : JpaRepository<BlogOwner, Long> {

   //  If more than one level downwards, there will be duplicates. Bug?
   // Alternative for queries:
   // https://www.linkedin.com/pulse/easy-dynamic-queries-kotlin-rafael-mariano-de-oliveira/
   // More sophisticated approach with JOOQ, dislikes codegen really
   // https://thorben-janssen.com/hibernate-jooq-a-match-made-in-heaven/
   //  If more than one level downwards and MutableList is used, there will be duplicates. Bug?
   // Fetches all, but not really required

    @EntityGraph(attributePaths = ["blogs","blogs.topic","blogs.topic.language","blogs.blogPosts"])
    override fun findById(id: Long): Optional<BlogOwner>

    fun findBlogOwnerByUsername(user: String): BlogOwner?
}