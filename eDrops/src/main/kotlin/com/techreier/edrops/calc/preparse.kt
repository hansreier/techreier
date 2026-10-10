package com.techreier.edrops.calc

import com.techreier.edrops.calc.Op.Companion.basicOperators
import com.techreier.edrops.calc.Op.Companion.symbols
import java.text.ParsePosition

//one time parse through expression
fun preparse(calc: Calc<*>, expr: String): ParseResult {
    var i1: Int
    var i2: Int
    var number: Number?
    var oper: Oper? = null
    var pLevel = 0
    var leftpPos = 0
    var whitespace = false
    var lastOpType = OpType.EMPTY
    val tokens = mutableListOf<Token>()
    var parseError: ParseError?

    val pos = ParsePosition(0)
    do {
        i1 = pos.index
        while ((i1 < expr.length - 1) && (expr[i1].isWhitespace())) {
            whitespace = true
            i1++
        }
        pos.index = i1
        val minus = ((expr).isNotEmpty() && (expr[pos.index] == '-'))

        number = if (!minus || whitespace) { //skip number if it is the minus operator
            when (calc.type) {
                Double::class.javaObjectType -> {
                    parseDouble(expr, pos)
                }
                else -> throw Exception("Not implemented") //TODO ReierArk implement for other calculator types
            }
        } else null

        i2 = pos.index
        if (i2 <= i1) {
            oper = findOperator(expr, i1, calc.operators)
        }

        if (number == null) {   //operator (including variables and separators)

            if ((oper == null)) {
                parseError = ParseError(UNPARSEABLE, i1)
                return ParseResult( tokens, parseError)
            }
            when (oper.op) {
                Op.SEPARATOR -> {
                    if (pLevel <= 0) {
                        parseError = ParseError(SEPARATOR_NOT_IN_FUNCTION, i1, oper.abbrev())
                        return ParseResult( tokens, parseError)
                    }
                    if ((lastOpType == OpType.OPERATOR) || (lastOpType == OpType.LEFTP) || (lastOpType == OpType.SEPARATOR)) {
                        parseError = ParseError(MISPLACED_SEPARATOR, i1, oper.abbrev())
                        return ParseResult( tokens, parseError)
                    }
                    lastOpType = OpType.SEPARATOR
                }

                Op.LEFTP -> {
                    if (lastOpType == OpType.SEPARATOR) {
                        parseError = ParseError(MISPLACED_PARENTHESIS, i1, oper.abbrev())
                        return ParseResult( tokens, parseError)
                    }
                    if (pos.index == expr.length - 1) {
                        parseError = ParseError(MISPLACED_PARENTHESIS, i1, oper.abbrev())
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
                        parseError = ParseError(MISPLACED_SEPARATOR, i1 - 1, oper.abbrev())
                        return ParseResult( tokens, parseError)
                    }
                    if (lastOpType == OpType.LEFTP) {
                        parseError = ParseError(EMPTY_PARENTHESIS, i1, oper.abbrev())
                        return ParseResult(tokens, parseError)
                    }
                    if (lastOpType == OpType.OPERATOR) {
                        parseError = ParseError(MISPLACED_OPERATOR, i1 - 1, oper.abbrev())
                        return ParseResult(tokens, parseError)
                    }
                    if (pos.index == 0) {
                        parseError = ParseError(MISPLACED_PARENTHESIS, i1, oper.abbrev())
                        return ParseResult( tokens, parseError)
                    }
                    pLevel--
                    lastOpType = OpType.RIGHTP
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
                        parseError = ParseError(MISSING_OPERAND, i1, oper.abbrev())
                        return ParseResult(tokens, parseError)
                    }
                    if (lastOpType == OpType.SEPARATOR) {
                        parseError = ParseError(MISSING_OPERAND, i1, oper.abbrev())
                        return ParseResult(tokens, parseError)
                    }
                    if ((pos.index == 0) || pos.index == expr.length - 1) {
                        parseError = ParseError(MISPLACED_OPERATOR, i1, oper.abbrev())
                        return ParseResult(tokens, parseError)
                    }
                    lastOpType = OpType.OPERATOR
                }

                else -> {

                    if (lastOpType == OpType.FUNCTION) {
                        parseError = ParseError(CONSECUTIVE_FUNCTIONS, i1, oper.abbrev())
                        return ParseResult(tokens, parseError)
                    }
                    lastOpType = OpType.FUNCTION
                }
            }
            if (pLevel < 0) {
                parseError = ParseError(TOO_MANY_RIGHT_PARENTHESIS, i1)
                return ParseResult( tokens, parseError)
            }
            tokens.add(OperatorToken(oper.op, oper.pos))
            pos.index = i1 + oper.abbrev().length
        } else { //number

            if (lastOpType == OpType.SYMBOL) {
                parseError = ParseError(MISSING_OPERATOR, i1)
                return ParseResult(tokens, parseError)
            }
            if (lastOpType == OpType.NUMBER) {
                parseError = ParseError(MISSING_OPERATOR, i1)
                return ParseResult( tokens, parseError)
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

fun findOperator(text: String, pos: Int, set: Set<Op>): Oper? {
    val foundOp = Op.operator(text, pos, set) ?: return null
    return Oper(foundOp, pos)
}

data class ParseResult(val tokens: List<Token>, val parseError: ParseError?) {}