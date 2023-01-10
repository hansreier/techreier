package com.sigmondsmart.edrops.repository

import com.sigmondsmart.edrops.config.logger
import com.sigmondsmart.edrops.domain.Blog
import com.sigmondsmart.edrops.domain.BlogData
import com.sigmondsmart.edrops.domain.BlogOwner
import org.assertj.core.api.Assertions.assertThat
import org.hibernate.annotations.QueryHints
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit.jupiter.SpringExtension
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.persistence.Subgraph





@ExtendWith(SpringExtension::class)
@DataJpaTest
class TestOwner {

    @PersistenceContext
    private lateinit var entityManager: EntityManager

    @Autowired
    lateinit var entryRepo: BlogEntryRepository

    @Autowired
    lateinit var blogRepo: BlogRepository

    @Autowired
    lateinit var ownerRepo: BlogOwnerRepository

    @Autowired
    lateinit var languageRepo: LanguageRepository

    lateinit var blogData: BlogData
    @BeforeEach
    fun setup() {
        blogData = BlogData()
        languageRepo.save(blogData.norwegian)
        ownerRepo.save(blogData.blogOwner)
    }

    @Test
    @DirtiesContext
    fun `cascade delete test`() {
        with(blogData) {
            logger.info("starting transactional test")
            val blogEntrySaved = entryRepo.findByIdOrNull(blogEntry.id)
            assertThat(blogEntrySaved?.id).isEqualTo(blogEntry.id)
            logger.info("blogEntry: $blogEntry")
            ownerRepo.delete(blogOwner)
            logger.info("Reier Deleted")
            val blogDeleted = blogRepo.findByIdOrNull(blog1.id)
            assertThat(blogDeleted).isNull()
            val blogEntryDeleted = entryRepo.findByIdOrNull(blogEntry.id)
            assertThat(blogEntryDeleted).isNull()
            logger.info("completed")
            ownerRepo.flush()
            blogOwner.blogs?.clear() // or else inconsistency if more processing
        }
    }

    @Test
    @DirtiesContext
    fun `read all with JPQL test`() {
        with(blogData) {
            logger.info("starting transactional test")
            entityManager.clear()
            logger.info("saved")
            val blogs = populate(blogOwner)
            logger.info("blog: $blogs ${blogs.size}")
            assertThat(blogs.size).isEqualTo(2)
            blogs.forEach {
                logger.info("my blog: $it")
                it.blogEntries?.forEach {
                    logger.info("my blogentry: $it")
                }
            }
        }
    }

    //Using JPQL instead of typesafe JPA criteria queries (too much work for nothing)
    //Or user Kotlin JDSL?
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
        queryBlog.setParameter(1, blogOwner).setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
        val blogs = queryBlog.resultList

        val queryBlog2 = entityManager.createQuery(
            "SELECT DISTINCT b FROM Blog b"
                    + " INNER JOIN FETCH b.blogOwner o"
                    + " WHERE b in :blogs "
        )
        queryBlog2.setParameter("blogs", blogs).setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
        val result = queryBlog2.resultList
        logger.info("populate end")
        @Suppress("UNCHECKED_CAST")
        return result as MutableList<Blog>
    }

    @Test
    @DirtiesContext
    fun `read all with findById test`() {
        with(blogData) {
            logger.info("starting read all test")
            entityManager.clear()
            logger.info("saved")
            val owner = ownerRepo.findByIdOrNull(blogOwner.id)
            logger.info("owner: $owner $owner?.blogEntries?.size}")
            assertThat(owner?.blogs?.size).isEqualTo(2)
            val blogs = owner?.blogs
            logger.info("my blogs: $blogs")
            blogs?.forEach {
                logger.info("my blog: $it")
                it.blogEntries?.forEach {
                    logger.info("my blogentry: $it")
                }
            }
        }
    }

    @Test
    @DirtiesContext
    //Works, generates only one SQL
    fun `read all with findById2 test`() {
        with(blogData) {
            logger.info("starting read all test")
            entityManager.clear()
            logger.info("saved")
            val owner = blogOwner.id?.let { ownerRepo.findById(it) }?.orElse(null)
            logger.info("owner: $owner $owner?.blogEntries?.size}")
            assertThat(owner?.blogs?.size).isEqualTo(2)
            val blogs = owner?.blogs
            logger.info("my blogs: $blogs")
            blogs?.forEach {
                logger.info("my blog: $it")
                it.blogEntries?.forEach {
                    logger.info("my blogentry: $it")
                }
            }
        }
    }

    @Test
    @DirtiesContext
    //https://www.baeldung.com/jpa-entity-graph
    fun `read with entityManager find manual entityGraph`() {
        with(blogData) {
            logger.info("starting read all test")
            entityManager.clear()
            val entityGraph = entityManager.createEntityGraph(BlogOwner::class.java)
            val blogGraph: Subgraph<Blog> = entityGraph.addSubgraph("blogs")
            entityGraph.addAttributeNodes("blogs")
            blogGraph.addAttributeNodes("language")
            blogGraph.addAttributeNodes("blogEntries")
            val hints: MutableMap<String, Any> = HashMap()
            hints["javax.persistence.fetchgraph"] = entityGraph
            logger.info("saved")
            val blogOwner = entityManager.find(BlogOwner::class.java, blog1.id, hints)
            logger.info("Blog language: ${blogOwner.blogs?.first()?.language} owner: ${blogOwner.id}" + "" +
                    " entries: ${blogOwner.blogs?.first()?.blogEntries}")
        }
    }
}