package com.techreier.edrops.data

import com.techreier.edrops.domain.LanguageCode
import com.techreier.edrops.domain.Topic
import com.techreier.edrops.util.checkDuplicates

const val NORWEGIAN = "Norwegian"
const val ENGLISH = "English"
const val NB = "nb"
const val EN = "en"
const val DEFAULT_LANGCODE = EN

const val TOPIC_DEFAULT = "default"
const val TOPIC_CODING = "coding"
const val TOPIC_TECH = "tech"
const val TOPIC_ENERGY = "energy"
const val TOPIC_LEISURE = "leisure"
const val TOPIC_POLITICS = "politics"

// Initialize languages and topics
class Base {
    val norwegian: LanguageCode = LanguageCode(NORWEGIAN, NB)
    val english: LanguageCode = LanguageCode(ENGLISH, EN)
    val languages = listOf(norwegian, english)

    val defaultNo = Topic(TOPIC_DEFAULT, norwegian, 0)
    val defaultEn = Topic(TOPIC_DEFAULT, english, 0)
    val codingNo = Topic(TOPIC_CODING, norwegian, 1)
    val codingEn = Topic(TOPIC_CODING, english, 1)
    val techNo = Topic(TOPIC_TECH, norwegian,2)
    val techEn = Topic(TOPIC_TECH, english, 2 )
    val energyNo = Topic(TOPIC_ENERGY, norwegian, 3)
    val energyEn = Topic(TOPIC_ENERGY, english, 3)
    val leisureNo = Topic(TOPIC_LEISURE, norwegian, 4)
    val leisureEn = Topic(TOPIC_LEISURE, english, 4)
    val politicsNo = Topic(TOPIC_POLITICS, norwegian, 5)
    val politicsEn = Topic(TOPIC_POLITICS, english, 5)

    val topics = mutableListOf(defaultNo, defaultEn, codingNo, codingEn,  techNo, techEn, energyNo, energyEn,
        leisureNo, leisureEn, politicsNo, politicsEn)

    init {
        topics.checkDuplicates { it.topicKey to it.language.code  }
    }

}
