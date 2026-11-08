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
import com.techreier.edrops.calc.isPow
import com.techreier.edrops.calc.isRightP
import com.techreier.edrops.calc.isVariable
import com.techreier.edrops.exceptions.ParseException
import java.text.ParsePosition
import java.util.*

// Standard methods like ParseDouble, Scanner and NumberFormat and Regex all fails
// Instructed AI agent and it wrote it by hand like this, like I would have done it
fun parseDouble(expr: String?, pos: ParsePosition? = ParsePosition(0)): Double? {
    if (expr == null || pos == null) return null
    val start = pos.index
    if (start < 0 || start >= expr.length) return null
    val len = expr.length
    var i = start

    // Optional leading minus sign, must be directly followed by digits or a decimal separator.
    // A leading '+' is never part of a number, it is always an operator.
    if (expr[i] == '-') i++

    val intDigitsStart = i
    while (i < len && expr[i].isDigit()) i++
    val intDigitCount = i - intDigitsStart

    // Optional fractional part, accepting both norwegian comma and english dot.
    // A dangling separator without trailing digits (e.g. "3.") is still consumed, matching Double.parseDouble.
    var fracDigitCount = 0
    if (i < len && (expr[i] == '.' || expr[i] == ',')) {
        i++
        val fracDigitsStart = i
        while (i < len && expr[i].isDigit()) i++
        fracDigitCount = i - fracDigitsStart
    }

    if (intDigitCount == 0 && fracDigitCount == 0) return null

    // Optional exponent part, e.g. 1E-5 or 1e+5
    if (i < len && (expr[i] == 'e' || expr[i] == 'E')) {
        var j = i + 1
        if (j < len && (expr[j] == '+' || expr[j] == '-')) j++
        val expDigitsStart = j
        while (j < len && expr[j].isDigit()) j++
        if (j > expDigitsStart) i = j
    }

    val numberText = expr.substring(start, i).replace(',', '.')
    val result = numberText.toDoubleOrNull() ?: return null
    pos.index = i
    return result
}



// Tilleggsfunksjon for Double beregninger
fun Calc<Double>.radians(enabled: Boolean): Calc<Double> {
    if (this is CalcDouble) {
        this.radians = enabled
    }
    return this
}

// Return error indicator of expression given a parseError
fun errorIndicator(expr: String, parseError: ParseError?): String? {
    val cleanExpr = expr
        .replace("\r\n", " ")
        .replace('\n', ' ')
        .replace('\r', ' ')
        .replace('\t', ' ')
        .replace('\u00A0', ' ')
    val errPosition = parseError?.position ?: return null
    val startPos = (errPosition - 40).coerceIn(0, cleanExpr.length)
    val endPos = (errPosition + 40).coerceIn(0, cleanExpr.length)
    return "${cleanExpr.substring(startPos, errPosition)}???" +
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
    var push = true
    if (range.max < range.min) throw IllegalStateException("min ${range.min} > max ${range.max}")

    for (i in range.min..range.max) {
        val token = tokens[i]
        when {
            token.isLeftP() -> {
                balance++; push = false
            }

            token.isRightP() -> {
                balance--; push = false
            }

            token.isFunction() -> push = false
            (token is OperatorToken) && (!token.isVariable()) -> {
                if (balance == 0) {
                    if ((token.operator.prior() >= prior) && !token.isPow()) {
                        return OperatorInfo(i - 1, push)
                    } else {
                        push = false
                    }
                }
            }
        }
    }
    return OperatorInfo(range.max, push)
}

//Split multiExpresssion based on separator ;
fun splitMultiExpr(text: String): List<Pair<String, Int>> {
    val result = mutableListOf<Pair<String, Int>>()
    var depth = 0
    var startIndex = 0
    for (i in text.indices) {
        when (text[i]) {
            '(', '[', '{' -> depth++
            ')', ']', '}' -> if (depth > 0) depth--
            ';' -> {
                if (depth == 0) {
                    result.add(Pair(text.substring(startIndex, i), startIndex))
                    startIndex = i + 1
                }
            }
        }
    }
    if (startIndex < text.length) {
        result.add(Pair(text.substring(startIndex), startIndex))
    }
    return result
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

data class Range(val min: Int, val max: Int) {
    init {
        require(min >= 0 && max >= 0) { "Range values cannot be negative: min=$min, max=$max" }
    }
}

data class ArgumentInfo(val range: Range, val nextPos: Int, val tokens: List<Token>, val push: Boolean)

data class OperatorInfo(val endPos: Int, val push: Boolean)

operator fun <T> ArrayList<T>.set(op: Op, value: T) {
    this[op.ordinal] = value
}

operator fun <T> ArrayList<T>.get(op: Op): T {
    return this[op.ordinal]
}

fun String.sanitize(): String = this
    .replace("\r\n", " ")
    .replace('\n', ' ')
    .replace('\r', ' ')
    .replace('\t', ' ')
    .replace('\u00A0', ' ').trim()
