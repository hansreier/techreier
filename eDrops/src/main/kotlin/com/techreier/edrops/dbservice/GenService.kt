package com.techreier.edrops.dbservice

import com.techreier.edrops.config.logger
import com.techreier.edrops.domain.BlogOwner
import com.techreier.edrops.domain.LanguageCode
import com.techreier.edrops.domain.Topic
import com.techreier.edrops.repository.BlogOwnerRepository
import com.techreier.edrops.repository.LanguageRepository
import com.techreier.edrops.repository.TopicRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class GenService(
    private val ownerRepo: BlogOwnerRepository,
    private val languageRepo: LanguageRepository,
    private val topicRepo: TopicRepository,
) {
    fun readOwner(blogOwnerId: Long): BlogOwner? {
        logger.info("Read blog owner")
        return ownerRepo.findById(blogOwnerId).orElse(null)
    }

    fun readLanguages(): MutableList<LanguageCode> = languageRepo.findAll()

    fun readTopics(languageCode: String): MutableList<Topic> {
        val topics = topicRepo.findAllByLanguageCodeOrderByPos(languageCode)
        return topics
    }
}
