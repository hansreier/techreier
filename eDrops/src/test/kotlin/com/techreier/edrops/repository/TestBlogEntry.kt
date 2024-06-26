package com.techreier.edrops.repository

import com.techreier.edrops.config.logger
import com.techreier.edrops.domain.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@ExtendWith(SpringExtension::class)
@SpringBootTest
@Transactional
class TestBlogEntry: Base() {

    @Test
    @DirtiesContext
    fun `basic CRUD checks`() {
        logger.info("Basic crud test")
        with(blogData) {
            blogEntry1.title = TITLE4MOD
            entryRepo.saveAndFlush(blogEntry1)
            val readBlogEntry = entryRepo.findByIdOrNull(2)
            assertThat(readBlogEntry?.id).isEqualTo(2)
            val blogs = entryRepo.findAll(Sort.by(Sort.Direction.ASC, "id"))
            assertThat(blogs).hasSize(noOfBlogEntries)
            assertThat(blogs[0].title).isEqualTo(TITLE4MOD)
            assertThat(blogs[1].title).isEqualTo(TITLE2)
            val foundBlogs = entryRepo.findByTitle(TITLE4MOD)
            assertThat(foundBlogs).hasSize(1)
            assertThat(foundBlogs.first().title).isEqualTo(TITLE4MOD)
            logger.info("t: ${entryRepo.count()}")
        }
    }

    @Test
    @DirtiesContext
    fun `change contents check`() {
        with(blogData) {
            logger.info("blogOwner: $blogOwner")
            val newTime = LocalDateTime.now()
            blogEntry1.title = TITLE2
            blogEntry1.changed = newTime
            assertThat(blogEntry1.title).isEqualTo(TITLE2)
            assertThat(blogEntry1.changed).isEqualTo(newTime)
            val blog = entryRepo.findAll(Sort.by(Sort.Direction.ASC, "id"))
            assertThat(blog).hasSize(noOfBlogEntries)
            assertThat(blog[1].title).isEqualTo(TITLE2)
            logger.info("blog entry: ${blog[0]}")
        }
    }
}



