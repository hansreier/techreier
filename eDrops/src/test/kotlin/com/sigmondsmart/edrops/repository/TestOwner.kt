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
    fun `read all with JPQL test`() {
        with(blogData) {
            logger.info("starting transactional test")
            entityManager.clear()
            logger.info("saved")
            val myblogs = populate(blogOwner)
            logger.info("blog: $myblogs ${myblogs.blogs?.size}")
            assertThat(myblogs.blogs?.size).isEqualTo(2)
            val entries = blog.blogEntries
            logger.info("my entries: $entries")
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
    private fun populate(blogOwner: BlogOwner): BlogOwner {
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
        val blogOwnerReturned = (queryBlog2.singleResult as Blog).blogOwner
        logger.info("populate end")
        return blogOwnerReturned
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
}