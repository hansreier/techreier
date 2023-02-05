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
    lateinit var blogRepo: BlogRepository

    @Autowired
    lateinit var blogTextRepo: BlogTextRepository

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
            blogEntry1.id?.let {
                blogTextRepo.saveAndFlush(BlogText(TEXT1, blogEntry1, it))
            }
            val blogTextFound = blogTextRepo.findByIdOrNull(blogEntry1.id)
            assertThat(blogTextFound).isNotNull
            assertThat(blogTextFound?.text).isEqualTo(TEXT1)
            blogTextFound?.text = TEXT2
            blogTextRepo.flush()
            val blogTexts = blogTextRepo.findAll()
            assertThat(blogTexts).isNotNull
            assertThat(blogTexts.size).isEqualTo(1)
            assertThat(blogTexts[0]?.text).isEqualTo(TEXT2)
            blogTextRepo.delete(blogTexts[0])
            assertThat(blogTextRepo.count()).isEqualTo(0)
        }
    }
}



