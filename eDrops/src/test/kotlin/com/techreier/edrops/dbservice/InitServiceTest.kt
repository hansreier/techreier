package com.techreier.edrops.dbservice

import com.techreier.edrops.data.Base
import com.techreier.edrops.data.Initial
import com.techreier.edrops.config.AppConfig
import com.techreier.edrops.config.logger
import com.techreier.edrops.data.TOPIC_DEFAULT
import com.techreier.edrops.domain.Topic
import com.techreier.edrops.repository.BlogOwnerRepository
import com.techreier.edrops.repository.BlogRepository
import com.techreier.edrops.repository.LanguageRepository
import com.techreier.edrops.repository.TopicRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import org.junit.jupiter.api.Test

@SpringBootTest
@Transactional
class InitServiceTest {

    @Autowired
    private lateinit var initService: InitService

    @Autowired
    private lateinit var blogOwnerRepo: BlogOwnerRepository

    @Autowired
    private lateinit var blogRepo: BlogRepository

    @Autowired
    private lateinit var languageRepo: LanguageRepository

    @Autowired
    private lateinit var topicRepo: TopicRepository

    @Autowired
    private lateinit var appConfig: AppConfig

    @Test
    fun checkSpringConttext() {
        logger.info("Spring Context OK")
    }

    @Test
    fun happyServiceTest() {
        val base = Base()
        val initial = Initial(appConfig, base)
        val owner = initial.blogOwner.username
        initService.saveInitialData(initial)
        val blogOwner = blogOwnerRepo.findBlogOwnerByUsername(owner)
    }

    @Test
    fun duplicateTopicServiceTest() {
        val base = Base()
        val initial = Initial(appConfig, base)
        languageRepo.saveAll(base.languages)

        val topic = Topic(TOPIC_DEFAULT, base.norwegian, 0)

        topicRepo.save(topic)
        initService.saveInitialData(initial)
    }

    @Test
    fun duplicateBlogSegmentTest() {
        val base = Base()
        val initial = Initial(appConfig, base)
        languageRepo.saveAll(base.languages)

     //   blogRepo.save(Blog)

        initService.saveInitialData(initial)
    }
}