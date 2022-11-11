package com.sigmondsmart.edrops.repository

import com.sigmondsmart.edrops.config.log
import com.sigmondsmart.edrops.domain.BlogEntry
import com.sigmondsmart.edrops.domain.BlogOwner
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDateTime

@ExtendWith(SpringExtension::class)
@DataJpaTest
@Disabled
class TestBlog() {

    @Autowired
    lateinit var entryRepo: BlogEntryRepository

    @Autowired
    lateinit var ownerRepo: BlogOwnerRepository

    @Test
    fun `owner with blog entry check` () {
        log.info("reiers starting transactional test")
        val blogOwner = BlogOwner(LocalDateTime.now(), null,
        "Hans Reier","Sigmond", "reier.sigmond@gmail.com",
        "+4791668863","Sløttvegen 17","2390","Moelv")
        val blogEntry = BlogEntry(LocalDateTime.now(), null, "Min første blog", blogOwner)
        ownerRepo.save(blogOwner)
        entryRepo.save(blogEntry)
        log.info("completed")
    }
}