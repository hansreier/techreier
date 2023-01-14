package com.sigmondsmart.edrops.repository

import com.sigmondsmart.edrops.config.logger
import com.sigmondsmart.edrops.domain.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@DataJpaTest
class TestBlogText {

    @Autowired
    lateinit var ownerRepo: BlogOwnerRepository

    @Autowired
    lateinit var languageRepo: LanguageRepository

    @Autowired
    lateinit var blogTextRepo: BlogTextRepository

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
            blogEntry.id?.let {
                blogTextRepo.saveAndFlush(BlogText(TEXT1, blogEntry, it))
            }
            val blogTextFound = blogTextRepo.findByIdOrNull(blogEntry.id)
            assertThat(blogTextFound).isNotNull
            assertThat(blogTextFound?.text).isEqualTo(TEXT1)
        }
    }
}



