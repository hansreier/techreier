package com.sigmondsmart.edrops.repository

import com.sigmondsmart.edrops.config.logger
import com.sigmondsmart.edrops.domain.BlogData
import com.sigmondsmart.edrops.domain.BlogOwner
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit.jupiter.SpringExtension
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext


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

    @Test
    @DirtiesContext
    fun `cascade delete test`() {
        val blogData = BlogData()
        with(blogData) {
            logger.info("starting transactional test")
            ownerRepo.save(blogOwner)
            val blogEntrySaved = entryRepo.findByIdOrNull(blogEntry.id)
            assertThat(blogEntrySaved?.id).isEqualTo(blogEntry.id)
            logger.info("blogEntry: $blogEntry")
            ownerRepo.delete(blogOwner)
            logger.info("Reier Deleted")
            val blogDeleted = blogRepo.findByIdOrNull(blog.id)
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
    @Disabled
    fun `read all with JPQL test`() {
        val blogData = BlogData()
        with(blogData) {
            logger.info("starting transactional test")
            ownerRepo.save(blogOwner)
            entityManager.clear()
            logger.info("saved")
            val myblogs = blog.id?.let { populate(it) }
            logger.info("blog: $myblogs ${myblogs?.blogs?.size}")
            assertThat(myblogs?.blogs?.size).isEqualTo(1)
            val entries = blog.blogEntries
            logger.info("my entries: $entries")
        }
    }

    //Using JPQL instead of typesafe JPA criteria queries (too much work for nothing)
    //Or user Kotlin JDSL?
    // find.. does not seem to populate children
    //TODO does not work
    //https://stackoverflow.com/questions/30088649/how-to-use-multiple-join-fetch-in-one-jpql-query
    //https://vladmihalcea.com/hibernate-facts-multi-level-fetching/
    //https://stackoverflow.com/questions/6562673/onetomany-list-vs-set-difference
    //https://thorben-janssen.com/association-mappings-bag-list-set/
    //https://dzone.com/articles/why-set-is-better-than-list-in-manytomany
    private fun populate(id: Long): BlogOwner {
        val query = entityManager.createQuery("SELECT DISTINCT o,b FROM BlogOwner o, Blog b"
                + " INNER JOIN FETCH o.blogs "
                + " INNER JOIN FETCH b.blogEntries "
                + " WHERE o.id = ?1 ")
        query.setParameter(1, id)
        return query.singleResult as BlogOwner
    }

    @Test
    @DirtiesContext
    fun `read all with findById test`() {
        val blogData = BlogData()
        with(blogData) {
            logger.info("starting read all test")
            ownerRepo.save(blogOwner)
            entityManager.clear()
            logger.info("saved")
            val owner = ownerRepo.findByIdOrNull(blogOwner.id)
            logger.info("owner: $owner $owner?.blogEntries?.size}")
            assertThat(owner?.blogs?.size).isEqualTo(1)
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
}