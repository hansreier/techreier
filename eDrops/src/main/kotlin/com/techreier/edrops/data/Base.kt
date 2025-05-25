package com.techreier.edrops.data

import com.techreier.edrops.domain.LanguageCode
import com.techreier.edrops.domain.Topic

const val NORWEGIAN = "Norwegian"
const val ENGLISH = "English"
const val NB = "nb"
const val EN = "en"
const val DEFAULT_LANGCODE = EN

const val TOPIC_DEFAULT = "default"
const val TOPIC_CODING = "coding"
const val TOPIC_ENERGY = "energy"
const val TOPIC_SPORT = "sport"

const val SUBMENU_MIN_ITEMS = 2 //Minimum number of items within a topic sub menu
const val MENU_SPLIT_SIZE = 10 //Minimum menu size before splitting

class Base {
    val norwegian: LanguageCode = LanguageCode(NORWEGIAN, NB)
    val english: LanguageCode = LanguageCode(ENGLISH, EN)
    val languages = listOf(norwegian, english)

    val defaultNo = Topic(TOPIC_DEFAULT, norwegian, 0)
    val defaultEn = Topic(TOPIC_DEFAULT, english, 0)
    val codingNo = Topic(TOPIC_CODING, norwegian, 1)
    val codingEn = Topic(TOPIC_CODING, english, 1)
    val energyNo = Topic(TOPIC_ENERGY, norwegian, 2)
    val energyEn = Topic(TOPIC_ENERGY, english, 2)
    val sportNo = Topic(TOPIC_SPORT, norwegian, 3)
    val sportEn = Topic(TOPIC_SPORT, english, 3)
    val politicsNo = Topic(TOPIC_CODING, norwegian, 4)
    val politicsEn = Topic(TOPIC_CODING, english, 4)

    val topics = listOf(defaultNo, defaultEn, codingNo, codingEn, energyNo, energyEn, sportNo, sportEn,politicsNo, politicsEn)
}
