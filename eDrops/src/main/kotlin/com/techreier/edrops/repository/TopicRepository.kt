package com.techreier.edrops.repository

import com.techreier.edrops.domain.Topic
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface TopicRepository : JpaRepository<Topic, Long> {

    @EntityGraph(attributePaths = ["language"])
    fun findAllByLanguageCodeOrderByPos(languageCode: String): MutableList<Topic>

    @EntityGraph(attributePaths = ["language"])
    fun findByTopicKeyAndLanguageCode(topicKey: String, languageCode: String): Optional<Topic>
}
