package com.sigmondsmart.edrops.repository

import com.sigmondsmart.edrops.config.logger
import com.sigmondsmart.edrops.domain.Blog
import com.sigmondsmart.edrops.domain.BlogData
import com.sigmondsmart.edrops.domain.NO
import com.sigmondsmart.edrops.domain.NORWEGIAN
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import kotlin.collections.HashMap


@ExtendWith(SpringExtension::class)
@DataJpaTest
class TestBlog {

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
            blogRepo.delete(blog1)
            logger.info("Reier Deleted")
            val blogEntryDeleted = entryRepo.findByIdOrNull(blogEntry.id)
            assertThat(blogEntryDeleted).isNull()
            logger.info("completed")
            blogRepo.flush()
            blog1.blogEntries?.clear() // or else inconsistency if more processing
        }
    }

    @Test
    @DirtiesContext
    fun `read all with JPQL test`() {
        with(blogData) {
            logger.info("starting transactional test")
            entityManager.clear()
            logger.info("saved")
            val blog = blog1.id?.let { populate(it) }
            logger.info("blog: $blog ${blog?.blogEntries?.size}")
            assertThat(blog?.blogEntries?.size).isEqualTo(2)
            assertThat(blog?.language?.language).isEqualTo(NORWEGIAN)
            assertThat(blog?.language?.code).isEqualTo(NO)
            val entries = blog?.blogEntries
            logger.info("my entries: $entries")
        }
    }

    //Using JPQL instead of typesafe JPA criteria queries (too much work for nothing)
    //Or user Kotlin JDSL?
    // find.. does not seem to populate children
    private fun populate(id: Long): Blog {
        val query = entityManager.createQuery("SELECT DISTINCT b FROM Blog b INNER JOIN FETCH b.blogEntries WHERE b.id = ?1 ")
        return query.setParameter(1, id).singleResult as Blog
    }

    @Test
    @DirtiesContext
    //Works but generates more SQL statements
    //Problem seems to be that JPA annotation does not apply to extension function
    //I did many attempts to get it to work.
    fun `read all with findById test`() {
        with(blogData) {
            logger.info("starting read all test")
            entityManager.clear()
            logger.info("saved")
            val blog = blogRepo.findByIdOrNull(blogOwner.id)
            logger.info("blog: $blog ${blog?.blogEntries?.size}")
            assertThat(blog?.blogEntries?.size).isEqualTo(2)
            val entries = blog?.blogEntries
            logger.info("my entries: $entries")
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
            val blog = blogOwner.id?.let { blogRepo.findById(it) }?.orElse(null)
            logger.info("blog: $blog ${blog?.blogEntries?.size}")
            assertThat(blog?.blogEntries?.size).isEqualTo(2)
            val entries = blog?.blogEntries
            logger.info("my entries: $entries")
        }
    }

    @Test
    @DirtiesContext
    //Using JPQL more efficient, only one SQL statement
    //https://www.baeldung.com/spring-data-jpa-named-entity-graphs
    fun `read all with findAll test`() {
        with(blogData) {
            logger.info("starting read all test")
            entityManager.clear()
            logger.info("saved")
            val blog = blogRepo.findAll()
            logger.info("blog: $blog ${blog.size}")
            blog.forEach {
                val entries = it.blogEntries
                logger.info("my entries: $entries")
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
            val entityGraph = entityManager.createEntityGraph(Blog::class.java)
            entityGraph.addAttributeNodes("language")
            entityGraph.addAttributeNodes("blogOwner")
            entityGraph.addAttributeNodes("blogEntries")
            val hints: MutableMap<String, Any> = HashMap()
            hints["javax.persistence.fetchgraph"] = entityGraph
            logger.info("saved")
            val blog = entityManager.find(Blog::class.java, blog1.id, hints)
            assertThat(blog?.blogEntries?.size).isEqualTo(2)
            logger.info("Blog language: ${blog.language.language} owner: ${blog.blogOwner.id} entries: ${blog.blogEntries}")
        }
    }
}