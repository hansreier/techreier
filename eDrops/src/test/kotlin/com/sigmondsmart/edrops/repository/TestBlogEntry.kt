package com.sigmondsmart.edrops.repository

import com.sigmondsmart.edrops.config.logger
import com.sigmondsmart.edrops.domain.BlogData
import com.sigmondsmart.edrops.domain.ENTRY3
import com.sigmondsmart.edrops.domain.ENTRY2
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
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
    fun `basic CRUD checks`() {
        logger.info("Basic crud test")
        with(blogData) {
            blogEntry.text = ENTRY3
            val readBlogEntry = entryRepo.findByIdOrNull(2)
            assertThat(readBlogEntry?.id).isEqualTo(2)
            val blogs = entryRepo.findAll(Sort.by(Sort.Direction.ASC, "id"))
            assertThat(blogs).hasSize(2)
            assertThat(blogs[0].text).isEqualTo(ENTRY3)
            assertThat(blogs[1].text).isEqualTo(ENTRY2)
            val foundBlogs = entryRepo.findByText(ENTRY3)
            assertThat(foundBlogs).hasSize(1)
            assertThat(foundBlogs.first().text).isEqualTo(ENTRY3)
            blog.blogEntries?.removeLast()
            assertThat(entryRepo.count()).isEqualTo(1)
        }
    }

    @Test
    @DirtiesContext
    fun `change contents check`() {
        with(blogData) {
            logger.info("blogOwner: $blogOwner")
            val newTime = LocalDateTime.now()
            blogEntry.text = ENTRY2
            blogEntry.changed = newTime
            assertThat(blogEntry.text).isEqualTo(ENTRY2)
            assertThat(blogEntry.changed).isEqualTo(newTime)
            val blog = entryRepo.findAll(Sort.by(Sort.Direction.ASC, "id"))
            assertThat(blog).hasSize(2)
            assertThat(blog[0].text).isEqualTo(ENTRY2)
            logger.info("blog entry: ${blog[0]}")
        }
    }
}



