package com.techreier.edrops.repository

import com.techreier.edrops.config.logger
import com.techreier.edrops.domain.BlogEntry
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

@SpringBootTest
@Transactional
class BlogEntryTest : TestBase() {

    @Test
    fun `basic CRUD checks`() {
        logger.info("Basic crud test start create")
        var blogEntry1 = BlogEntry(
            ZonedDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS),
            "Katt", "Pus", "Min katt er huskatt", blog
        )
        blogEntry1 = entryRepo.saveAndFlush(blogEntry1)
        blog.blogEntries.add(blogEntry1)
        assertNotNull(blogEntry1)
        assertNotNull(blogEntry1.id)
        entityManager.clear()
        logger.info("Basic crud test start read")
        val blogEntry2 = entryRepo.findById(blogEntry1.id!!).orElse(null)
        assertNotNull(blogEntry2)
        assertNotNull(blogEntry2.id)
        assertEquals(blogEntry1.changed, blogEntry2.changed)
        //   assertThat(blogEntry2).usingRecursiveComparison().isEqualTo(blogEntry1)
        logger.info("Basic crud test start update")
        blogEntry1.title = "Pusur"
        entryRepo.save(blogEntry1)
        val blogList = entryRepo.findByTitle("Pusur")
        assertThat(blogList.size).isEqualTo(1)
        assertNotNull(blogList[0])
        assertNotNull(blogList[0].id)
        assertEquals("Pusur", blogList[0].title)
        logger.info("Basic crud test start delete")
        entryRepo.delete(blogEntry1)
        val notFound = entryRepo.findById(blogEntry1.id).orElse(null)
        assertNull(notFound)
    }
}


