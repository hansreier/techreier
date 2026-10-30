package com.techreier.edrops.calc

import com.techreier.edrops.calc.Op.Companion.basicOperators
import com.techreier.edrops.calc.Op.Companion.symbols
import com.techreier.edrops.calc.utils.parseDouble
import java.text.ParsePosition

//One time parse through expression, expr must be sanitized and trimmed
fun <T> preparse(calc: Calc<T>, expr: String): ParseResult {
    var i1: Int
    var i2: Int
    var number: Number?
    var oper: Oper? = null
    var pLevel = 0
    var leftpPos = 0
    var lastOpType = OpType.EMPTY
    val tokens = arrayListOf<Token>()
    var parseError: ParseError?
    val pos = ParsePosition(0)
    do {
        i1 = pos.index
        while ((i1 < expr.length - 1) && (expr[i1].isWhitespace())) {
            i1++
        }
        pos.index = i1

        val minus = ((expr).isNotEmpty() && (expr[pos.index] == '-'))

        number =
            when (calc.type) { // parse text to number based on number format allowed in the calculator
                Double::class.javaObjectType -> {
                    parseDouble(expr, pos)
                }

                else -> TODO("Implement later")
            }

        if ((number == null) && minus && ((lastOpType == OpType.LEFTP) || lastOpType == OpType.OPERATOR
                    || lastOpType == OpType.NEGATE  || lastOpType == OpType.EMPTY || lastOpType == OpType.SEPARATOR)) {
            oper = Oper(Op.NEGATE, i1)
            i1 = i1 - Op.NEGATE.abbrev().length + 1
        } else {
            i2 = pos.index
            if (i2 <= i1) { // Find operator if it exists
                val foundOp = Op.operator(expr, i1, calc.operators)
                oper = foundOp?.let { Oper(foundOp, pos.index) } //TODO ReierAsk Variables must be defines three places
            }
        }

        if (number == null) {   // Operator (including variables and separators)

            if ((oper == null)) {
                parseError = ParseError(UNPARSEABLE, i1)
                return ParseResult(tokens, parseError)
            }
            when (oper.op) {
                Op.SEPARATOR -> {
                    if (pLevel <= 0) {
                        parseError = ParseError(SEPARATOR_NOT_IN_FUNCTION, i1, oper.op.abbrev())
                        return ParseResult(tokens, parseError)
                    }
                    if ((lastOpType == OpType.OPERATOR) || (lastOpType == OpType.LEFTP) || (lastOpType == OpType.SEPARATOR)) {
                        parseError = ParseError(MISPLACED_SEPARATOR, i1, oper.op.abbrev())
                        return ParseResult(tokens, parseError)
                    }
                    lastOpType = OpType.SEPARATOR
                }

                Op.LEFTP -> {
                    if (lastOpType == OpType.SEPARATOR) {
                        parseError = ParseError(MISPLACED_PARENTHESIS, i1, oper.op.abbrev())
                        return ParseResult(tokens, parseError)
                    }
                    if (pos.index == expr.length - 1) {
                        parseError = ParseError(MISPLACED_PARENTHESIS, i1, oper.op.abbrev())
                        return ParseResult(tokens, parseError)
                    }
                    if (lastOpType == OpType.NUMBER || lastOpType == OpType.SYMBOL || lastOpType == OpType.RIGHTP) {
                        tokens.add(OperatorToken(Op.MULTIPLY, i1))
                    }
                    pLevel++; leftpPos = i1
                    lastOpType = OpType.LEFTP
                }

                Op.RIGHTP -> {
                    if (lastOpType == OpType.SEPARATOR) {
                        parseError = ParseError(MISPLACED_SEPARATOR, i1 - 1, oper.op.abbrev())
                        return ParseResult(tokens, parseError)
                    }
                    if (lastOpType == OpType.LEFTP) {
                        parseError = ParseError(EMPTY_PARENTHESIS, i1, oper.op.abbrev())
                        return ParseResult(tokens, parseError)
                    }
                    if (lastOpType == OpType.OPERATOR) {
                        parseError = ParseError(MISPLACED_OPERATOR, i1 - 1, oper.op.abbrev())
                        return ParseResult(tokens, parseError)
                    }
                    if (pos.index == 0) {
                        parseError = ParseError(MISPLACED_PARENTHESIS, i1, oper.op.abbrev())
                        return ParseResult(tokens, parseError)
                    }
                    pLevel--
                    lastOpType = OpType.RIGHTP
                }

                Op.NEGATE -> {
                    if (lastOpType == OpType.NEGATE){ //Todo ReierAsk
                        parseError = ParseError(CONSECUTIVE_NEGATE, i1, oper.op.abbrev())
                        return ParseResult(tokens, parseError)
                    }
                    lastOpType = OpType.NEGATE
                }

                in symbols -> {
                    if (lastOpType == OpType.SYMBOL) {
                        tokens.add(OperatorToken(Op.MULTIPLY, i1))
                    }
                    if (lastOpType == OpType.NUMBER) {
                        tokens.add(OperatorToken(Op.MULTIPLY, i1))
                    }

                    if (lastOpType == OpType.RIGHTP) {
                        tokens.add(OperatorToken(Op.MULTIPLY, i1))
                    }
                    lastOpType = OpType.SYMBOL
                }

                in basicOperators -> {
                    if (lastOpType == OpType.OPERATOR) {
                        parseError = ParseError(MISSING_OPERAND, i1, oper.op.abbrev())
                        return ParseResult(tokens, parseError)
                    }
                    if (lastOpType == OpType.SEPARATOR) {
                        parseError = ParseError(MISSING_OPERAND, i1, oper.op.abbrev())
                        return ParseResult(tokens, parseError)
                    }
                    if ((pos.index == 0) || pos.index == expr.length - 1) {
                        parseError = ParseError(MISPLACED_OPERATOR, i1, oper.op.abbrev())
                        return ParseResult(tokens, parseError)
                    }
                    lastOpType = OpType.OPERATOR
                }

                else -> { //TODO ReierAsk check what to do with this problem for functions without arguments

                    if (lastOpType == OpType.FUNCTION){
                        parseError = ParseError(CONSECUTIVE_FUNCTIONS, i1, oper.op.abbrev())
                        return ParseResult(tokens, parseError)
                    }
                    lastOpType = OpType.FUNCTION
                }
            }
            if (pLevel < 0) {
                parseError = ParseError(TOO_MANY_RIGHT_PARENTHESIS, i1)
                return ParseResult(tokens, parseError)
            }
            tokens.add(OperatorToken(oper.op, oper.pos))
            pos.index = i1 + oper.op.abbrev().length
        } else { //number

            if (lastOpType == OpType.SYMBOL) {
                parseError = ParseError(MISSING_OPERATOR, i1)
                return ParseResult(tokens, parseError)
            }
            if (lastOpType == OpType.NUMBER) {
                parseError = ParseError(MISSING_OPERATOR, i1)
                return ParseResult(tokens, parseError)
            }
            if (lastOpType == OpType.RIGHTP) {
                tokens.add(OperatorToken(Op.MULTIPLY, i1))
            }
            lastOpType = OpType.NUMBER
            tokens.add(NumberToken(number, i1))
        }
    } while (pos.index < expr.length)

    if (pLevel > 0) {
        parseError = ParseError(TOO_MANY_LEFT_PARENTHESIS, leftpPos)
        return ParseResult(tokens, parseError)
    }
    return ParseResult(tokens, null)
}

private data class Oper(
    val op: Op,
    val pos: Int,
)

data class ParseResult(val tokens: List<Token>, val parseError: ParseError?)

