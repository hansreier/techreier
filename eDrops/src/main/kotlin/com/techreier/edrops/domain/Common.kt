package com.techreier.edrops.domain

const val NORWEGIAN = "Norwegian"
const val ENGLISH = "English"
const val NB = "nb"
const val EN = "en"
const val DEFAULT = "default"
const val CODING = "coding"
const val ENERGY = "energy"

class Common {
    val norwegian: LanguageCode = LanguageCode(NORWEGIAN, NB)
    val english: LanguageCode = LanguageCode(ENGLISH, EN)

    val languages = listOf(norwegian, english)

    val defaultNo = Topic(DEFAULT, norwegian, 0)
    val defaultEn = Topic(DEFAULT, english, 1)
    val codingNo = Topic(CODING, norwegian, 1)
    val codingEn = Topic(CODING, english, 1)
    val energyNo = Topic(ENERGY, norwegian, 2)
    val energyEn = Topic(ENERGY, english, 2)

    val topics = listOf(defaultNo, defaultEn, codingNo, codingEn, energyNo, energyEn)
}
