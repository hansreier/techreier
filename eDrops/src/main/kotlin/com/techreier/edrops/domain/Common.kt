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

    val defaultNo = Topic(DEFAULT, norwegian)
    val defaultEn = Topic(DEFAULT, english)
    val codingNo = Topic(CODING, norwegian)
    val codingEn = Topic(CODING, english)
    val energyNo = Topic(ENERGY, norwegian)
    val energyEn = Topic(ENERGY, english)

    val topics = listOf(defaultNo, defaultEn, codingNo, codingEn, energyNo, energyEn)
}
