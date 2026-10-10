package com.techreier.edrops.calc

import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.ParsePosition
import java.util.*


fun parseDouble(expr: String?, pos: ParsePosition? = ParsePosition(0)): Double? {
    if (expr == null || pos == null || pos.index >= expr.length) return null
    val initialIndex = pos.index

    val remaining = expr.substring(pos.index)
    val preferEnglish = remaining.contains('.')

    val primaryFormat =
        (NumberFormat.getInstance(if (preferEnglish) Locale.ENGLISH else Locale.forLanguageTag("nb")) as DecimalFormat).apply {
            isGroupingUsed = false
        }

    val parseResult = primaryFormat.parse(expr, pos)

    val result = if (parseResult != null) {
        parseResult
    } else {
        val fallbackFormat = NumberFormat.getInstance(
            if (preferEnglish) Locale.forLanguageTag("nb") else Locale.ENGLISH
        ) as DecimalFormat
        fallbackFormat.isGroupingUsed = false
        pos.index = initialIndex
        fallbackFormat.parse(expr, pos) ?: return null
    }
    return result.toDouble()
}

// Tilleggsfunksjon for Double beregninger
fun Calc<Double>.degrees(enabled: Boolean): Calc<Double> {
    if (this is CalcDouble) {
        this.degrees = enabled
    }
    return this
}

// Return error indicator of expessen given a parseError
fun errorIndicator(expr: String, parseError: ParseError?): String? {
    val errPosition = parseError?.position ?: return null
    val startPos = (errPosition - 40).coerceIn(0, expr.length)
    val endPos = (errPosition + 40).coerceIn(0, expr.length)
    return "${expr.substring(startPos, errPosition)}???" +
            if (errPosition == expr.length) "" else expr.substring(errPosition, endPos)
}

fun errorText(expr: String, parseError : ParseError?): String? {
    parseError ?: return null
    return "${parseError.key}: ${errorIndicator(expr, parseError)}"
}