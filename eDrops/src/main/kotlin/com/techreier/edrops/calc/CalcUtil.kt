package com.techreier.edrops.calc

import com.techreier.edrops.exceptions.ParseException
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

fun removeOuterP(rangeIn: Range, tokens: List<Token>): Range {
    var min = rangeIn.min
    var max = rangeIn.max
    while (min < max &&
        tokens[min].isLeftP() &&
        tokens[max].isRightP()
    ) {
        if (!wrapsWhole(min, max, tokens)) break
        min++
        max--
    }
    return Range(min, max)
}

private fun wrapsWhole(min: Int, max: Int, tokens: List<Token>): Boolean {
    var balance = 0
    for (i in min..max) {
        when {
            tokens[i].isLeftP() -> balance++
            tokens[i].isRightP() -> balance--
        }
        if (balance == 0) return (i == max)
    }
    return false
}

fun findUnboundOperator(range: Range, prior: Int, tokens: List<Token>): OperatorInfo {
    var balance = 0
    var push = false

    for (i in range.min..range.max) {
        val token = tokens[i]
        when {
            token.isLeftP() -> {balance++ ; push = true }
            token.isRightP() -> {balance-- ; push = true }
            token.isFunction() -> push = true
            (token is OperatorToken) && (!token.isVariable()) -> {
                if (balance == 0) {
                    if (token.operator.prior() >= prior) {
                        return OperatorInfo(i - 1,  push)
                    } else {
                        push = true
                    }
                }
            }
        }
    }
    return OperatorInfo( range.max,   push)
}

fun parseArguments(range: Range, tokens: List<Token>): ArgumentInfo {
    var balance = 0
    var push = true
    val arguments = ArrayList<Token>()
    var i = range.min + 1
    var min = range.min
    var max = range.max
    val operatorText = (tokens[range.min] as OperatorToken).operator.abbrev()
    logger.debug("parse arguments for $operatorText range=$range expr=${tokens.toExpr(range)}")
    do {
        when {
            tokens[i].isValue() -> arguments.add(tokens[i])
            tokens[i].isLeftP() -> {balance++; if (balance == 1) min = i + 1 }
            tokens[i].isRightP() -> {balance--; if (balance == 0) max = i - 1}
            tokens[i].isSeparator() -> {}
            tokens[i].isFunction() -> { push = false}
            tokens[i].isOperator() -> { push = false}
            else -> arguments.add(tokens[i])
        }
        i++
    } while ((i <= range.max) && balance > 0)
    if ((balance != 0) && push) {
        val error = ParseError(MISPLACED_PARENTHESIS, tokens[range.min].position, operatorText)
        throw ParseException("Error parsing arguments", error)
    }
    logger.debug("arguments found: ${arguments.size} range=$range ")
    return ArgumentInfo(Range(min, max), arguments, push)
}

fun findClosure(range: Range, tokens: List<Token>): ArgumentInfo {
    var balance = 0
    var closingIndex = range.max
    var error: ParseError

    if (!tokens[range.min].isLeftP()) {
        error = ParseError(MISSING_PARENTHESIS, tokens[range.min].position, oper=Op.LEFTP.abbrev())
        throw ParseException("Error finding closure of parenthesis", error)
    }

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
        throw ParseException("Error with closure of parenthesis", error)
    }

    return ArgumentInfo(Range(range.min, closingIndex), emptyList(), false)
}

data class Range(val min: Int, val max: Int)

data class ArgumentInfo(val range: Range, val tokens: List<Token>, val push: Boolean)

data class OperatorInfo(val endPos: Int,  val push: Boolean)

operator fun <T> ArrayList<T>.set(op: Op, value: T) {
    this[op.ordinal] = value
}

operator fun <T> ArrayList<T>.get(op: Op): T {
    return this[op.ordinal]
}