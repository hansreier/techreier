package com.techreier.edrops.dbservice

import com.techreier.edrops.domain.Topic
import com.techreier.edrops.repository.TopicRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class TopicService(
    private val topicRepo: TopicRepository,
) {

    fun saveAll(topics: List<Topic>) {
        topicRepo.saveAll(topics)
    }

    fun findAllByLanguageCode(languageCode: String): List<Topic> {
        return topicRepo.findAllByLanguageCodeOrderByPos(languageCode)
    }

    fun findByTopicKeyAndLanguageCode(topicKey: String, languageCode: String): Topic? {
        return topicRepo.findFirstByTopicKeyAndLanguageCode(topicKey, languageCode).orElse(null)
    }
}

