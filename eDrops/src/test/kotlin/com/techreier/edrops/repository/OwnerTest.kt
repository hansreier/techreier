package com.techreier.edrops.repository

import com.techreier.edrops.config.logger
import com.techreier.edrops.domain.Blog
import com.techreier.edrops.domain.BlogOwner
import com.techreier.edrops.domain.Topic
import jakarta.persistence.Subgraph
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
class OwnerTest : TestBase() {

    @Test
    fun `cascade delete test`() {
        logger.info("starting transactional test")
        val blogEntrySaved = entryRepo.findByIdOrNull(blogEntryId)
        assertThat(blogEntrySaved?.id).isEqualTo(blogEntryId)
        ownerRepo.delete(blogOwner)
        logger.info("Reier Deleted")
        val blogDeleted = blogRepo.findByIdOrNull(blog.id)
        assertThat(blogDeleted).isNull()
        val blogEntryDeleted = entryRepo.findByIdOrNull(blogEntryId)
        assertThat(blogEntryDeleted).isNull()
        logger.info("completed")
       // ownerRepo.flush()
    }


    @Test
    fun `read all with JPQL test`() {
        logger.info("starting transactional test")
        entityManager.clear()
        logger.info("saved")
        val blogs = populate(blogOwner)
        logger.info("blog: $blogs ${blogs.size}")
        assertThat(blogs.size).isEqualTo(noOfBlogs)
        blogs.forEach {
            logger.info("my blog: $it")
            it.blogEntries.forEach {
                logger.info("my blogentry: $it")
            }
        }

    }

    //Using JPQL instead of typesafe JPA criteria queries (too much work for nothing)
    // find.. does not seem to populate children
    //But !! Does not generate one single SQL, this I what I wanted
    //https://stackoverflow.com/questions/30088649/how-to-use-multiple-join-fetch-in-one-jpql-query
    //https://vladmihalcea.com/hibernate-facts-multi-level-fetching/
    //https://stackoverflow.com/questions/6562673/onetomany-list-vs-set-difference
    //https://thorben-janssen.com/association-mappings-bag-list-set/
    //https://dzone.com/articles/why-set-is-better-than-list-in-manytomany
    private fun populate(blogOwner: BlogOwner): List<Blog> {
        logger.info("populate start")
        val queryBlog = entityManager.createQuery(
            "SELECT DISTINCT b FROM Blog b"
                    + " INNER JOIN FETCH b.blogEntries"
                    + " WHERE b.blogOwner = ?1 "
        )
        //setHint(QueryHints.PASS_DISTINCT_THROUGH, false) not required hibernate 6 (default)
        queryBlog.setParameter(1, blogOwner)
        val blogs = queryBlog.resultList

        val queryBlog2 = entityManager.createQuery(
            "SELECT DISTINCT b FROM Blog b"
                    + " INNER JOIN FETCH b.blogOwner o"
                    + " WHERE b in :blogs "
        )
        queryBlog2.setParameter("blogs", blogs)
        val result = queryBlog2.resultList
        logger.info("populate end")
        @Suppress("UNCHECKED_CAST")
        return result as MutableList<Blog>
    }


    @Test
    fun `read all with findByIdOrNull test`() {
        logger.info("starting read all test")
        entityManager.clear()
        val owner = ownerRepo.findByIdOrNull(blogOwner.id!!)
        logger.info("owner: $owner $owner?.blogEntries?.size}")
        assertThat(owner?.blogs?.size).isEqualTo(noOfBlogs)
        val blogs = owner?.blogs
        logger.info("my blogs: $blogs")
        blogs?.forEach {
            logger.info("my blog: $it")
            it.blogEntries.forEach {
                logger.info("my blogentry: $it")
            }
        }

    }


    @Test
    //Works, generates less SQLs
    fun `read all with findById test`() {
        logger.info("starting read all test")
        entityManager.clear()
        logger.info("saved")
        val owner = blogOwner.id?.let { ownerRepo.findById(it) }?.orElse(null)
        logger.info("owner: $owner $owner?.blogEntries?.size}")
        assertThat(owner?.blogs?.size).isEqualTo(noOfBlogs)
        val blogs = owner?.blogs
        logger.info("my blogs: $blogs")
        var count = 0
        blogs?.forEach {
            logger.info("my blog: $it")
            it.blogEntries.forEach {
                logger.info("my blogentry: $it")
                count++
            }
        }
        assertThat(count).isEqualTo(noOfBlogEntries)
    }


    @Test
    //https://www.baeldung.com/jpa-entity-graph
    //Correct result only if blogs is defined as a set and not as a list in blogOwner
    //Problem is that hibernate generate left outer joins.
    //Not anymore returning duplicates in hibernate 6, but 3 more SQL calls instead.
    fun `read with entityManager find manual entityGraph`() {
        logger.info("starting read all test")
        entityManager.clear()
        val entityGraph = entityManager.createEntityGraph(BlogOwner::class.java)
        val blogGraph: Subgraph<Blog> = entityGraph.addSubgraph("blogs")
        val topicGraph: Subgraph<Topic> = blogGraph.addSubgraph("topic")
        topicGraph.addAttributeNodes("language")
        blogGraph.addAttributeNodes("blogEntries")
        val hints: MutableMap<String, Any> = HashMap()
        hints["javax.persistence.loadgraph"] = entityGraph //Fetchgraph or loadgraph (uses defaults)
        logger.info("saved")
        val blogOwner = entityManager.find(BlogOwner::class.java, blogOwner.id, hints)
        logger.info(
            "Blog language: ${blogOwner.blogs.first().topic.language} owner: ${blogOwner.id}" + "" +
                    " entries: ${blogOwner.blogs.first().blogEntries}"
        )
    }
}