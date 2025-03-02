package com.techreier.edrops.domain

const val NORWEGIAN = "Norwegian"
const val ENGLISH = "English"
const val NB = "nb"
const val EN = "en"
const val TOPIC_DEFAULT = "default"
const val TOPIC_CODING = "coding"
const val TOPIC_ENERGY = "energy"

class Common {
    val norwegian: LanguageCode = LanguageCode(NORWEGIAN, NB)

    val english: LanguageCode = LanguageCode(ENGLISH, EN)

    val languages = listOf(norwegian, english)

    val defaultNo = Topic(TOPIC_DEFAULT, norwegian, 0)
    val defaultEn = Topic(TOPIC_DEFAULT, english, 1)
    val codingNo = Topic(TOPIC_CODING, norwegian, 1)
    val codingEn = Topic(TOPIC_CODING, english, 1)
    val energyNo = Topic(TOPIC_ENERGY, norwegian, 2)
    val energyEn = Topic(TOPIC_ENERGY, english, 2)

    val topics = listOf(defaultNo, defaultEn, codingNo, codingEn, energyNo, energyEn)
}
