package com.sigmondsmart.edrops.repository

import com.sigmondsmart.edrops.config.log
import com.sigmondsmart.edrops.domain.BlogEntry
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@DataJpaTest
class TestBlog {

    @Autowired
    lateinit var entryRepo: BlogEntryRepository

    @Autowired
    lateinit var ownerRepo: BlogOwnerRepository

    @Test
    @DirtiesContext
    fun `cascade delete test`() {
        val blogData = BlogData()
        with(blogData) {
            log.info("reiers starting transactional test")
            ownerRepo.save(blogOwner)
            entryRepo.save(blogEntry)
            entryRepo.save(blogEntry2)
            val blogEntryList = mutableListOf<BlogEntry>()
            blogOwner.blogEntries  = blogEntryList
            blogOwner.blogEntries?.add(blogEntry)
            blogOwner.blogEntries?.add(blogEntry2)
            log.info("blogEntry: $blogEntry")
            ownerRepo.delete(blogOwner)
            log.info("Reier Deleted")
            val blogEntryDeleted = entryRepo.findByIdOrNull(blogEntry.id)
            assertThat(blogEntryDeleted).isNull()
            log.info("completed")
            ownerRepo.flush()
        }
    }
}