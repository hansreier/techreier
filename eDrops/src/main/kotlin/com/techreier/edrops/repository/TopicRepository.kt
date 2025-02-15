package com.techreier.edrops.repository

import com.techreier.edrops.domain.Topic
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TopicRepository : JpaRepository<Topic, Long> {

 //   @EntityGraph(attributePaths = [ "topic", "topic.language"])
    fun findAllByLanguageCode(languageCode: String): MutableList<Topic>
}