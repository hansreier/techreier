package com.techreier.edrops.calc.utils

import com.techreier.edrops.calc.*
import com.techreier.edrops.exceptions.ParseException
import com.techreier.edrops.graph.logger

fun parseArguments(rangeIn: Range, tokens: List<Token>): ArgumentInfo {
    var balance = 0
    var push = true
    val arguments = ArrayList<Token>()
    var i = rangeIn.min + 1
    var min = rangeIn.min + 1
    var max = rangeIn.max
    var parenthesis = false
    var up = false
    var updown = false
    val noExpectedArgs = (tokens[rangeIn.min] as OperatorToken).operator.noArgs()
    val operatorText = (tokens[rangeIn.min] as OperatorToken).operator.abbrev()
    logger.info("parse arguments for {} range={} expr={}", operatorText, rangeIn, tokens.toExpr(rangeIn))
    do {
        when {
            tokens[i].isValue() -> arguments.add(tokens[i])
            tokens[i].isLeftP() -> {
                balance++
                parenthesis = true
                if (up) updown = true
                if (balance == 1) min = i + 1
            }

            tokens[i].isRightP() -> {
                balance--
                parenthesis = true
                up = true
                if (balance == 0) max = i - 1
            }

            tokens[i].isSeparator() -> {}
            tokens[i].isFunction() -> {
                push = false
            }

            tokens[i].isOperator() -> {
                push = false
            }
       //     else -> arguments.add(tokens[i])

        }
        i++
    } while (((i <= rangeIn.max) && (balance > 0)) || (!parenthesis && (arguments.size < noExpectedArgs)))

val range = if (parenthesis) Range(min, max) else Range(min, min)

    logger.info("arguments found for {} range={} count: {}", operatorText, range, arguments.size)

    if (balance != 0) {
        val error = ParseError(MISPLACED_PARENTHESIS, tokens[range.min].position, operatorText)
        throw ParseException("Error parsing arguments", error)
    }

    if (push) {
        if (arguments.size != noExpectedArgs) {
            val error = ParseError(WRONG_ARGUMENT_COUNT, tokens[range.min].position, operatorText)
            throw ParseException("Error parsing arguments", error)
        }
        if (updown) {
            val error = ParseError(PARENTHESIS_MESS, tokens[range.min].position, operatorText)
            throw ParseException("Error parsing arguments", error)
        }

    }

    return ArgumentInfo(range, i, arguments, push)
}