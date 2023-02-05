package com.sigmondsmart.edrops.repository

import com.sigmondsmart.edrops.config.logger
import com.sigmondsmart.edrops.domain.*
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
    lateinit var blogRepo: BlogRepository

    @Autowired
    lateinit var languageRepo: LanguageRepository

    lateinit var blogData: BlogData
    @BeforeEach
    fun setup() {
        blogData = BlogData()
        languageRepo.save(blogData.norwegian)
        ownerRepo.save(blogData.blogOwner)
        blogRepo.save(blogData.blog1)
        blogRepo.save(blogData.blog2)
    }

    @Test
    @DirtiesContext
    fun `basic CRUD checks`() {
        logger.info("Basic crud test")
        with(blogData) {
            blogEntry1.title = ENTRYMOD
            val readBlogEntry = entryRepo.findByIdOrNull(2)
            assertThat(readBlogEntry?.id).isEqualTo(2)
            val blogs = entryRepo.findAll(Sort.by(Sort.Direction.ASC, "id"))
            assertThat(blogs).hasSize(NO_ENTRIES_TOTAL)
            assertThat(blogs[0].title).isEqualTo(ENTRYMOD)
            assertThat(blogs[1].title).isEqualTo(ENTRY2)
            val foundBlogs = entryRepo.findByTitle(ENTRYMOD)
            assertThat(foundBlogs).hasSize(1)
            assertThat(foundBlogs.first().title).isEqualTo(ENTRYMOD)
            blog1.blogEntries?.remove(blogEntry1)
            assertThat(entryRepo.count()).isEqualTo(NO_ENTRIES.toLong())
        }
    }

    @Test
    @DirtiesContext
    fun `change contents check`() {
        with(blogData) {
            logger.info("blogOwner: $blogOwner")
            val newTime = LocalDateTime.now()
            blogEntry1.title = ENTRY2
            blogEntry1.changed = newTime
            assertThat(blogEntry1.title).isEqualTo(ENTRY2)
            assertThat(blogEntry1.changed).isEqualTo(newTime)
            val blog = entryRepo.findAll(Sort.by(Sort.Direction.ASC, "id"))
            assertThat(blog).hasSize(NO_ENTRIES_TOTAL)
            assertThat(blog[0].title).isEqualTo(ENTRY2)
            logger.info("blog entry: ${blog[0]}")
        }
    }
}



