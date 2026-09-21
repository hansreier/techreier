package com.techreier.edrops.calc

import java.text.NumberFormat
import java.text.ParsePosition
import java.util.Locale

private val NO_FORMAT = NumberFormat.getInstance(Locale.forLanguageTag("nb"))
private val EN_FORMAT = NumberFormat.getInstance(Locale.ENGLISH)

fun parseNumber(expr: String?, pos: ParsePosition?): Number? {
    if (expr == null || pos == null) return null
    val initialIndex = pos.index
    val result = NO_FORMAT.parse(expr, pos)
    if (result != null) return result
    pos.index = initialIndex
    return EN_FORMAT.parse(expr, pos)
}