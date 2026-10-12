package com.techreier.edrops.calc

import com.techreier.edrops.graph.logger
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

fun errorText(expr: String, parseError: ParseError?): String? {
    parseError ?: return null
    return "${parseError.key}: ${errorIndicator(expr, parseError)}"
}

fun removeOuterP(range: Range, tokens: List<Token>): Range {
    var current = range
    while (current.min < current.max &&
        tokens[current.min].isLeftP() &&
        tokens[current.max].isRightP()
    ) {

        if (!wrapsWhole(current, tokens)) break

        current = Range(current.min + 1, current.max - 1)
    }
    return current
}

private fun wrapsWhole(range: Range, tokens: List<Token>): Boolean {
    var balance = 0
    for (i in range.min..range.max) {
        when {
            tokens[i].isLeftP() -> balance++
            tokens[i].isRightP() -> balance--
        }
        if (balance == 0) return i == range.max
    }
    return false
}

fun parseArguments(range: Range, tokens: List<Token>): ArgumentInfo {
    var error: ParseError? = null
    var balance = 0
    var push = true
    val arguments = ArrayList<Token>()
    var i = range.min + 1
    val operatorText = (tokens[range.min] as OperatorToken).operator.abbrev()
    logger.info("parse arguments for $operatorText range=$range")
    do {
        when {
            tokens[i].isValue() -> arguments.add(tokens[i])
            tokens[i].isLeftP() -> balance++
            tokens[i].isRightP() -> balance--
            tokens[i].isSeparator() -> {}
            tokens[i].isFunction() -> { push = false}
            tokens[i].isOperator() -> { push = false}
            else -> arguments.add(tokens[i])
        }
        i++
    } while ((i <= range.max) && balance > 0)

    if ((balance != 0) && push)  error =
        ParseError(MISPLACED_PARENTHESIS, tokens[range.min].position, operatorText)
    logger.info("arguments found: ${arguments.size} range=$range ")
    return ArgumentInfo(Range(range.min, i), arguments, push, error)
}

fun findClosure(range: Range, tokens: List<Token>): ArgumentInfo {
    var balance = 0
    var closingIndex = range.max
    var error: ParseError? = null

    for (i in range.min..range.max) {
        when {
            tokens[i].isLeftP() -> balance++
            tokens[i].isRightP() -> balance--
        }
        if (balance == 0) {
            closingIndex = i
            break
        }
    }

    if (balance != 0) {
        val operatorText = (tokens[range.min] as? OperatorToken)?.operator?.abbrev() ?: ""
        error = ParseError(MISPLACED_PARENTHESIS, tokens[range.min].position, operatorText)
    }

    return ArgumentInfo(Range(range.min, closingIndex), emptyList(), false, error)
}


data class Range(val min: Int, val max: Int)

data class ArgumentInfo(val range: Range, val tokens: List<Token>, val push: Boolean, val error: ParseError?)