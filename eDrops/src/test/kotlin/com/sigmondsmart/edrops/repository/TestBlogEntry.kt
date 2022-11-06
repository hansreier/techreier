package com.sigmondsmart.edrops.repository

import com.sigmondsmart.edrops.config.log
import com.sigmondsmart.edrops.domain.BlogEntry
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDateTime

@ExtendWith(SpringExtension::class)
@DataJpaTest
class TestBlogEntry() {

    @Autowired lateinit var repo: BlogEntryRepository

    @Test
    fun `basic entity checks`() {
        val p = BlogEntry(LocalDateTime.now(), LocalDateTime.now(),"MyBLog")
        val blogEntrySet = hashSetOf(p)
        repo.save(p)
        assertThat(repo.findAll()).hasSize(1)
        repo.findByText("MyBLog").forEach{
            log.info("tekst: ${it.text}")
        }

        log.info("Reier : ${repo.findByText("MyBLog")}")

        assertThat(blogEntrySet).contains(p)
    }
}


