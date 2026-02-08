package com.techreier.edrops.repository

import com.techreier.edrops.config.logger
import com.techreier.edrops.domain.BlogText
import com.techreier.edrops.domain.PostState
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.temporal.ChronoUnit


// TODO Consider where to put the test data, since BlogText in practice is not used yet.

const val BLOGTEXT_1 =
    "FN har 17 bærekraftmål der alle er like viktig "


const val BLOGTEXT_2 =
    "Først var det ikke snø. " +
            "Så snødde det mye. " +
            "Så kom det masse regn. " +
            "Så ble det isglatt og iskaldt. #Snø"

@ExtendWith(SpringExtension::class)
@SpringBootTest
@Transactional
class BlogTextTest : TestBase() {

    @Test
    fun `basic CRUD checks`() {
        logger.info("Basic crud test")
        blogPostId.let { //Just cannot use save here with partly persisted entities (change in Spring Boot 3.4).
            entityManager.persist(BlogText(Instant.now().truncatedTo(ChronoUnit.SECONDS),
                PostState.PUBLISHED.name,  BLOGTEXT_1, blogPost, it))
        }

        val blogTextFound = blogTextRepo.findByIdOrNull(blogPostId)
        assertThat(blogTextFound).isNotNull
        assertThat(blogTextFound?.text).isEqualTo(BLOGTEXT_1)
        blogTextFound?.text = BLOGTEXT_2
        blogTextRepo.flush()
        val blogTexts = blogTextRepo.findAll()
        assertThat(blogTexts).isNotNull
        assertThat(blogTexts.size).isEqualTo(1)
        assertThat(blogTexts[0]?.text).isEqualTo(BLOGTEXT_2)
        blogTextRepo.delete(blogTexts[0])
        assertThat(blogTextRepo.count()).isEqualTo(0)
    }

}



