package com.techreier.edrops.calc.utils

import com.techreier.edrops.calc.MISPLACED_PARENTHESIS
import com.techreier.edrops.calc.MISSING_PARENTHESIS
import com.techreier.edrops.calc.Op
import com.techreier.edrops.calc.OperatorToken
import com.techreier.edrops.calc.ParseError
import com.techreier.edrops.calc.Token
import com.techreier.edrops.calc.isLeftP
import com.techreier.edrops.calc.isRightP
import com.techreier.edrops.exceptions.ParseException

fun findClosure(range: Range, tokens: List<Token>): ArgumentInfo {
    var balance = 0
    var closingIndex = range.max
    var error: ParseError

    if (!tokens[range.min].isLeftP()) {
        error = ParseError(MISSING_PARENTHESIS, tokens[range.min].position, oper = Op.LEFTP.abbrev())
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

    return ArgumentInfo(Range(range.min, closingIndex), closingIndex+ 1, emptyList(), false)
}