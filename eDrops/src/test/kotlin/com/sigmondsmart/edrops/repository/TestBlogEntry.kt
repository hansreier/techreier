package com.sigmondsmart.edrops.repository

import com.sigmondsmart.edrops.config.log
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@ExtendWith(SpringExtension::class)
@DataJpaTest
class TestBlogEntry {

    @Autowired
    lateinit var repo: BlogEntryRepository

    @Autowired
    lateinit var ownerRepo: BlogOwnerRepository

    @Test
    @DirtiesContext
    fun `basic CRUD checks`() {
        log.info("Reier basic crud test")
        with(BlogData()) {
            ownerRepo.save(blogOwner)
            val saved = repo.save(blogEntry)
            blogEntry.text = MODIFIED_ENTRY
            repo.save(blogEntry)
            repo.save(blogEntry2)
            val readBlogEntry = repo.findByIdOrNull(2)
            assertThat(readBlogEntry?.id).isEqualTo(2)
            val blog = repo.findAll(Sort.by(Sort.Direction.ASC, "id"))
            assertThat(blog).hasSize(2)
            assertThat(blog[0].text).isEqualTo(MODIFIED_ENTRY)
            assertThat(blog[1].text).isEqualTo(SECOND_ENTRY)
            val foundBlogs = repo.findByText(MODIFIED_ENTRY)
            assertThat(foundBlogs).hasSize(1)
            assertThat(foundBlogs.first().text).isEqualTo(MODIFIED_ENTRY)
            saved.id?.let { repo.deleteById(it) }
            assertThat(repo.count()).isEqualTo(1)
        }
    }

    @Test
    @DirtiesContext
    fun `change contents check`() {
        with(BlogData()) {
            log.info("reiers starting transactional test")
            ownerRepo.save(blogOwner)
            repo.save(blogEntry)
            val newTime = LocalDateTime.now()
            blogEntry.text = SECOND_ENTRY
            blogEntry.changed = newTime
            assertThat(blogEntry.text).isEqualTo(SECOND_ENTRY)
            assertThat(blogEntry.changed).isEqualTo(newTime)
            val blog = repo.findAll(Sort.by(Sort.Direction.ASC, "id"))
            assertThat(blog).hasSize(1)
            assertThat(blog[0].text).isEqualTo(SECOND_ENTRY)
            log.info("blog entry: ${blog[0]}")
        }
    }
}



