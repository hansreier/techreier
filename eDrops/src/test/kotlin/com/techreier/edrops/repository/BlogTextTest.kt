package com.techreier.edrops.repository

import com.techreier.edrops.config.logger
import com.techreier.edrops.domain.BlogText
import com.techreier.edrops.domain.SUMMARY1
import com.techreier.edrops.domain.SUMMARY2
import com.techreier.edrops.util.timeStamp
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.annotation.Transactional

@ExtendWith(SpringExtension::class)
@SpringBootTest
@Transactional
class BlogTextTest : TestBase() {

    @Test
    fun `basic CRUD checks`() {
        logger.info("Basic crud test")
        blogEntryId.let {
            blogTextRepo.saveAndFlush(BlogText(timeStamp(), SUMMARY1, blogEntry, it))
        }
        val blogTextFound = blogTextRepo.findByIdOrNull(blogEntryId)
        assertThat(blogTextFound).isNotNull
        assertThat(blogTextFound?.text).isEqualTo(SUMMARY1)
        blogTextFound?.text = SUMMARY2
        blogTextRepo.flush()
        val blogTexts = blogTextRepo.findAll()
        assertThat(blogTexts).isNotNull
        assertThat(blogTexts.size).isEqualTo(1)
        assertThat(blogTexts[0]?.text).isEqualTo(SUMMARY2)
        blogTextRepo.delete(blogTexts[0])
        assertThat(blogTextRepo.count()).isEqualTo(0)
    }

}


