package com.sigmondsmart.edrops.repository

import com.sigmondsmart.edrops.config.log
import com.sigmondsmart.edrops.domain.BlogEntry
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDateTime

private const val FIRST_ENTRY = "My first blog"
private const val SECOND_ENTRY = "My next blog"
private const val MODIFIED_ENTRY = "Modified blog"

@ExtendWith(SpringExtension::class)
@DataJpaTest
class TestBlogEntry() {

    @Autowired lateinit var repo: BlogEntryRepository

    @Test
    fun `basic CRUD checks`() {
        val blogEntry = BlogEntry(LocalDateTime.now(), LocalDateTime.now(),FIRST_ENTRY)
        val blogEntry2 = BlogEntry(LocalDateTime.now(), LocalDateTime.now(),SECOND_ENTRY)
        val saved = repo.save(blogEntry)
        blogEntry.text = MODIFIED_ENTRY
        repo.save(blogEntry)
        repo.save(blogEntry2)
        val readBlogEntry = repo.findByIdOrNull(2)
        assertThat(readBlogEntry?.id).isEqualTo(2)
        val blog = repo.findAll(Sort.by(Sort.Direction.ASC,"id"))
        assertThat(blog).hasSize(2)
        assertThat(blog[0].text).isEqualTo(MODIFIED_ENTRY)
        assertThat(blog[1].text).isEqualTo(SECOND_ENTRY)
        val foundBlogs = repo.findByText(MODIFIED_ENTRY)
        assertThat(foundBlogs).hasSize(1)
        assertThat(foundBlogs.first().text).isEqualTo(MODIFIED_ENTRY)
        saved.id?.let { repo.deleteById(it) }
        assertThat(repo.count()).isEqualTo(1)
    }
    @Test
    fun `change contents check` () {
        log.info("reiers starting transactional test")
        val blogEntry = BlogEntry(LocalDateTime.now(), null,FIRST_ENTRY)
        repo.save(blogEntry)
        val newTime = LocalDateTime.now()
        blogEntry.text = SECOND_ENTRY
        blogEntry.changed = newTime
        assertThat(blogEntry.text).isEqualTo(SECOND_ENTRY)
        assertThat(blogEntry.changed).isEqualTo(newTime)
        val blog = repo.findAll(Sort.by(Sort.Direction.ASC,"id"))
        assertThat(blog).hasSize(1)
        assertThat(blog[0].text).isEqualTo(SECOND_ENTRY)
        log.info("blog entry: ${blog[0]}")
    }
}


