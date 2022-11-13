package com.sigmondsmart.edrops.repository

import com.sigmondsmart.edrops.config.logger
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDateTime

@ExtendWith(SpringExtension::class)
@DataJpaTest
class TestBlogEntry {

    @Autowired
    lateinit var entryRepo: BlogEntryRepository

    @Autowired
    lateinit var ownerRepo: BlogOwnerRepository

    @Test
    @DirtiesContext
    fun `basic CRUD checks`() {
        logger.info("Reier basic crud test")
        val blogData = BlogData()
        with(blogData) {
            ownerRepo.save(blogOwner)
            blogEntry.text = MODIFIED_ENTRY
            val readBlogEntry = entryRepo.findByIdOrNull(2)
            assertThat(readBlogEntry?.id).isEqualTo(2)
            val blog = entryRepo.findAll(Sort.by(Sort.Direction.ASC, "id"))
            assertThat(blog).hasSize(2)
            assertThat(blog[0].text).isEqualTo(MODIFIED_ENTRY)
            assertThat(blog[1].text).isEqualTo(SECOND_ENTRY)
            val foundBlogs = entryRepo.findByText(MODIFIED_ENTRY)
            assertThat(foundBlogs).hasSize(1)
            assertThat(foundBlogs.first().text).isEqualTo(MODIFIED_ENTRY)
            blogOwner.blogEntries?.removeLast()
            assertThat(entryRepo.count()).isEqualTo(1)
        }
    }

    @Test
    @DirtiesContext
    fun `change contents check`() {
        val blogData = BlogData()
        with(blogData) {
            logger.info("reiers starting transactional test")
            ownerRepo.save(blogOwner)
            logger.info("blogEntry: $blogEntry")
            val newTime = LocalDateTime.now()
            blogEntry.text = SECOND_ENTRY
            blogEntry.changed = newTime
            assertThat(blogEntry.text).isEqualTo(SECOND_ENTRY)
            assertThat(blogEntry.changed).isEqualTo(newTime)
            val blog = entryRepo.findAll(Sort.by(Sort.Direction.ASC, "id"))
            assertThat(blog).hasSize(2)
            assertThat(blog[0].text).isEqualTo(SECOND_ENTRY)
            logger.info("blog entry: ${blog[0]}")
        }
    }
}



