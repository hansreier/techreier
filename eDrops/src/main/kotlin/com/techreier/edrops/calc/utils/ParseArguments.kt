package com.techreier.edrops.calc.utils

import com.techreier.edrops.calc.MISPLACED_PARENTHESIS
import com.techreier.edrops.calc.OperatorToken
import com.techreier.edrops.calc.ParseError
import com.techreier.edrops.calc.Token
import com.techreier.edrops.calc.WRONG_ARGUMENT_COUNT
import com.techreier.edrops.calc.isFunction
import com.techreier.edrops.calc.isLeftP
import com.techreier.edrops.calc.isOperator
import com.techreier.edrops.calc.isRightP
import com.techreier.edrops.calc.isSeparator
import com.techreier.edrops.calc.isValue
import com.techreier.edrops.calc.toExpr
import com.techreier.edrops.exceptions.ParseException
import com.techreier.edrops.graph.logger
import java.util.ArrayList

fun parseArguments(range: Range, tokens: List<Token>): ArgumentInfo {
    var balance = 0
    var push = true
    val arguments = ArrayList<Token>()
    var i = range.min + 1
    var min = range.min + 1
    var max = range.max
    var parenthesis = false
    val noExpectedArgs = (tokens[range.min] as OperatorToken).operator.noArgs()
    val operatorText = (tokens[range.min] as OperatorToken).operator.abbrev()
    logger.info("parse arguments for {} range={} expr={}", operatorText, range, tokens.toExpr(range))
    do {
        when {
            tokens[i].isValue() -> arguments.add(tokens[i])
            tokens[i].isLeftP() -> {balance++;  min++; parenthesis = true }
            tokens[i].isRightP() -> {balance--; max--; parenthesis = true }
            tokens[i].isSeparator() -> {}
            tokens[i].isFunction() -> { push = false}
            tokens[i].isOperator() -> { push = false}
        }
        i++
    } while (((i <= range.max) && (balance > 0) ) || (!parenthesis && (arguments.size < noExpectedArgs)))

    if (!parenthesis) {
        max = min
    }
    val range = Range(min, max)
    logger.info("arguments found for {} range={} count: {}", operatorText, range, arguments.size)

    if ((balance != 0) && push) {
        val error = ParseError(MISPLACED_PARENTHESIS, tokens[range.min].position, operatorText)
        throw ParseException("Error parsing arguments", error)
    }
    if (arguments.size  != noExpectedArgs) {
        val error = ParseError(WRONG_ARGUMENT_COUNT, tokens[range.min].position, operatorText)
        throw ParseException("Error parsing arguments", error)
    }

    return ArgumentInfo(range,i, arguments, push)
}