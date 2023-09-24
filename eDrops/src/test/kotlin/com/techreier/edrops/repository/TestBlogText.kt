package com.techreier.edrops.repository

import com.techreier.edrops.config.logger
import com.techreier.edrops.domain.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@DataJpaTest
class TestBlogText : Base() {

    @Test
    @DirtiesContext
    fun `basic CRUD checks`() {
        logger.info("Basic crud test")
        with(blogData) {
            blogEntry1.id?.let {
                blogTextRepo.saveAndFlush(BlogText(SUMMARY1, blogEntry1, it))
            }
            val blogTextFound = blogTextRepo.findByIdOrNull(blogEntry1.id)
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
}



