package com.techreier.edrops.data.lab

import java.time.Instant

interface IExpression {
    fun no(): Expression
    fun en(): Expression
}

data class Expression(
    val timestamp: Instant,
    val segment: String,
    val pos: Int,
    val subject: String,
    val about: String,
    val expr: String,
    var langCode: String
)