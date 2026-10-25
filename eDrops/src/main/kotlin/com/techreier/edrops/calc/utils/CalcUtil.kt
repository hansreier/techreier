package com.techreier.edrops.calc.utils

import com.techreier.edrops.calc.Calc
import com.techreier.edrops.calc.CalcDouble
import com.techreier.edrops.calc.ILLEGAL_VARIABLES
import com.techreier.edrops.calc.Op
import com.techreier.edrops.calc.OperatorToken
import com.techreier.edrops.calc.ParseError
import com.techreier.edrops.calc.Token
import com.techreier.edrops.calc.isFunction
import com.techreier.edrops.calc.isLeftP
import com.techreier.edrops.calc.isRightP
import com.techreier.edrops.calc.isVariable
import com.techreier.edrops.exceptions.ParseException
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

// Return error indicator of expression given a parseError
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

fun checkVariables(calc: Calc<*>) {
    val illegalOps = calc.vars.keys.filter { it !in Op.variables }
    if (illegalOps.isNotEmpty()) {
        throw ParseException(
            "Illegal variables found",
            ParseError(ILLEGAL_VARIABLES, 0, illegalOps.joinToString(" "))
        )
    }
}

data class Range(val min: Int, val max: Int)

data class ArgumentInfo(val range: Range, val nextPos: Int, val tokens: List<Token>, val push: Boolean)

data class OperatorInfo(val endPos: Int,  val push: Boolean)

operator fun <T> ArrayList<T>.set(op: Op, value: T) {
    this[op.ordinal] = value
}

operator fun <T> ArrayList<T>.get(op: Op): T {
    return this[op.ordinal]
}