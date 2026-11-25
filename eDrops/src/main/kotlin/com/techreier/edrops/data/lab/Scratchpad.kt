package com.techreier.edrops.data.lab

import com.techreier.edrops.data.EN
import com.techreier.edrops.data.ENGLISH
import com.techreier.edrops.data.NB
import com.techreier.edrops.data.NORWEGIAN
import com.techreier.edrops.domain.LanguageCode
import com.techreier.edrops.util.timestamp

object Scratchpad : IExpression {
    val timestamp = timestamp("17.05.2025 07:00:00")
    const val SEGMENT = "scratchpad"
    const val SUBJECT_NO = "Kladd"
    const val SUBJECT_EN = "Scratchpad"
    const val POS = 1
    val norwegian: LanguageCode = LanguageCode(NORWEGIAN, NB)
    val english: LanguageCode = LanguageCode(ENGLISH, EN)
    override fun no() = Expression(timestamp, SEGMENT, POS, SUBJECT_NO, "", "", NB)
    override fun en() = Expression(timestamp, SEGMENT, POS, SUBJECT_EN, "", "", EN)
}