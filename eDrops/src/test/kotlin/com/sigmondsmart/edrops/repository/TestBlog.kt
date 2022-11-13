package com.sigmondsmart.edrops.repository

import com.sigmondsmart.edrops.config.log
import com.sigmondsmart.edrops.domain.BlogOwner
import org.assertj.core.api.Assertions.assertThat
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
class TestBlog {

    @PersistenceContext
    private lateinit var em: EntityManager

    @Autowired
    lateinit var entryRepo: BlogEntryRepository

    @Autowired
    lateinit var ownerRepo: BlogOwnerRepository

    @Test
    @DirtiesContext
    fun `cascade delete test`() {
        val blogData = BlogData()
        with(blogData) {
            log.info("starting transactional test")
            ownerRepo.save(blogOwner)
            entryRepo.save(blogEntry)
            entryRepo.save(blogEntry2)
            log.info("blogEntry: $blogEntry")
            ownerRepo.delete(blogOwner)
            log.info("Reier Deleted")
            val blogEntryDeleted = entryRepo.findByIdOrNull(blogEntry.id)
            assertThat(blogEntryDeleted).isNull()
            log.info("completed")
            ownerRepo.flush()
            blogOwner.blogEntries?.clear() // or else inconsistency if more processing
        }
    }

    @Test
    @DirtiesContext
    fun `read all with JPQL test`() {
        val blogData = BlogData()
        with(blogData) {
            log.info("starting transactional test")
            ownerRepo.save(blogOwner)
            em.clear()
            log.info("saved")
            val owner = blogOwner.id?.let { populate(it) }
            log.info("owner: $owner ${owner?.blogEntries?.size}")
            assertThat(owner?.blogEntries?.size).isEqualTo(2)
            val entries = blogOwner.blogEntries
            log.info("my entries: $entries")
        }
    }

    //Using JPQL instead of typesafe JPA criteria queries (too much work for nothing)
    //Or user Kotlin JDSL?
    // find.. does not seem to populate children
    private fun populate(id: Long): BlogOwner {
        val query = em.createQuery("SELECT DISTINCT o FROM BlogOwner o INNER JOIN FETCH o.blogEntries WHERE o.id = ?1 ")
        return query.setParameter(1, id).singleResult as BlogOwner
    }
}