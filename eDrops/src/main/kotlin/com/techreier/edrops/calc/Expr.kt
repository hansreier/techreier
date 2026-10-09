package com.techreier.edrops.calc

import com.techreier.edrops.calc.Op.Companion.basicOperators
import com.techreier.edrops.calc.Op.Companion.symbols
import com.techreier.edrops.config.logger
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.ParsePosition
import java.util.*

const val MISSING_OPERATOR = "MissingOperator"
const val UNPARSEABLE = "Unparseable"
const val EMPTY_PARENTHESIS = "EmptyParenthesis"
const val TOO_MANY_LEFT_PARANTHESIS = "TooManyLeftParenthesis"
const val TOO_MANY_RIGHT_PARANTHESIS = "TooManyRightParenthesis"
const val MISPLACED_OPERATOR = "MisplacedOperator"
const val MISSING_OPERAND = "MissingOperand"
const val WRONG_NO_OF_ARGUMENTS ="WrongNoOfArguments"

class Expr(val calc: Calc<*>, expr: String) {

    val expr: String
    var relaxed: Boolean
    val parseErrors = mutableListOf<ParseError>()
    val tokens = ArrayList<Token>()
    val xTokens = ArrayList<Token>()
    private var level: Int = 0
    private var resultSize = 0
    private var trace: Trace = Trace.OFF
    private var opStack = ArrayDeque<Oper>()
    private var noOfResults = 1

    init {
        this.expr = expr.trim()
        this.relaxed = false
        calc.logOp = false
        trace(expr)
    }

    fun rotateTraceLevel() {
        when (trace) {
            Trace.OFF -> {
                trace = Trace.STACK
                calc.logOp = true
                logger.info("trace stack")
            }

            Trace.STACK -> {
                trace = Trace.ALL
                calc.logOp = true
                logger.info("trace alt")
            }

            Trace.ALL -> {
                trace = Trace.OFF
                calc.logOp = false
                logger.info("trace av")
            }
        }
    }

    fun trace(trace: Trace) {
        this.trace = trace
        calc.logOp = trace != Trace.OFF
    }

    fun operator(text: String, pos: Int, set: Set<Op>): Oper? {
        val foundOp = Op.operator(text, pos, set) ?: return null
        return Oper(foundOp, pos)
    }

    private fun trace(text: String) {
        if (trace == Trace.ALL) {
            logger.info(text)
        }
    }

    private fun warn(text: String, relaxed: Boolean = false) {
        if (relaxed)
            logger.warn("warning: $text")
        else
            logger.error("error: $text")
    }

    fun errorIndicator(errPosition: Int): String {
        val startPos = (errPosition - 40).coerceIn(0, expr.length)
        val endPos = (errPosition + 40).coerceIn(0, expr.length)
        return "${expr.substring(startPos, errPosition)}???" +
                "${if (errPosition == expr.length) "" else expr.substring(errPosition, endPos)}"
    }


    private fun addToken(o: Oper): Boolean {
        trace("$o checking arguments:${o.noArgs()}")

        if (o.noArgs() != 0) {
            var noArgs = 0
            for (i in tokens.size - 1 downTo 0) {
                val t = tokens[i]
                if (t.position < o.pos) {
                    if (o.isBasic()) {
                        noArgs++
                    }
                    break
                }

                when (t) {
                    is NumberToken -> noArgs++
                    is OperatorToken -> noArgs -= (t.operator.noArgs() - t.operator.noResults())
                }
            }
            if (noArgs != o.noArgs()) { //TODO ReierAsk not possible toa add parameters here (No of arguments)
                parseErrors.add(ParseError(WRONG_NO_OF_ARGUMENTS, o.pos, o.abbrev()))
                return false
            }
        }

        resultSize += -o.op.noArgs() + o.op.noResults()
        tokens.add(OperatorToken(o.op, o.pos))
        trace("@adding token:$o level: $resultSize)")
        return true
    }

    private fun addP() {
        calc.operators.add(Op.LEFTP)
        calc.operators.add(Op.RIGHTP)
    }

    private fun delP() {
        calc.operators.remove(Op.LEFTP)
        calc.operators.remove(Op.RIGHTP)
    }

    fun calculate() {
        try {
            calc.clear() //clear the stack
            calc.logStack = (trace != Trace.OFF)
            for (t in tokens) {
                when (t) {
                    is NumberToken -> calc.enter(t.argument)
                    is OperatorToken -> if (calc.op(t.operator) == null) {
                        calc.addVarsOnEmptyStack()
                        break
                    }
                }
            }
        } finally {
            if (!calc.logStack) {
                calc.logStack = true
                calc.logStack()
            }
        }
    }

    //one time parse through expression
    fun preparse(): Boolean {
        var i1: Int
        var i2: Int
        var numbers = 0
        var number: Number?
        var pos: ParsePosition
        var oper: Oper? = null
        var pLevel = 0
        var leftpPos = 0
        var whitespace = false
        resultSize = 0
        var lastOpType = OpType.EMPTY
        addP()

        xTokens.clear()

        pos = ParsePosition(0)
        do {
            i1 = pos.index
            while ((i1 < expr.length -1) && (expr[i1].isWhitespace())) {
                whitespace = true
                i1++
            }
            pos.index = i1
            val minus = ((expr).isNotEmpty() && (expr[pos.index] == '-'))

            number = if (!minus || (whitespace && minus)) { //skip number if it is the minus operator
                when (calc.type) {
                    Double::class.javaObjectType -> {
                            parseDouble(expr, pos)
                    }
                    else -> throw Exception("Not implemented") //TODO ReierArk implement for other calculator types
                }
            } else null

            i2 = pos.index
            if (i2 <= i1) {
                oper = operator(expr, i1, calc.operators)
            }
            if (number != null) { //number
                if (lastOpType == OpType.SYMBOL) {
                    parseErrors.add(ParseError(MISSING_OPERATOR, i1))
                    return false
                }
                if (lastOpType == OpType.NUMBER) {
                    parseErrors.add(ParseError(MISSING_OPERATOR, i1))
                    return false
                }
                if (lastOpType == OpType.RIGHTP) {
                    xTokens.add(OperatorToken(Op.MULTIPLY, i1))
                }
                lastOpType = OpType.NUMBER
                xTokens.add(NumberToken(number, i1))
            } else { //operator (including variables and separators)
                if ((oper == null)) {
                    parseErrors.add(ParseError(UNPARSEABLE, i1))
                    return false
                }
                when (oper.op) {
                    Op.LEFTP -> {
                        pLevel++; leftpPos = i1
                        if (lastOpType == OpType.NUMBER || lastOpType == OpType.SYMBOL || lastOpType == OpType.RIGHTP) {
                            xTokens.add(OperatorToken(Op.MULTIPLY, i1))
                        }
                        lastOpType = OpType.LEFTP
                    }

                    Op.RIGHTP -> {
                        pLevel--
                        if (lastOpType == OpType.LEFTP  ) {
                            parseErrors.add(ParseError(EMPTY_PARENTHESIS, i1, oper.abbrev()))
                            return false
                        }
                        if (lastOpType == OpType.OPERATOR ) {
                            parseErrors.add(ParseError(MISPLACED_OPERATOR, i1, oper.abbrev()))
                            return false
                        }
                        lastOpType = OpType.RIGHTP
                    }

                    in symbols -> {
                        if (lastOpType == OpType.SYMBOL) {
                            xTokens.add(OperatorToken(Op.MULTIPLY, i1))
                        }
                        if (lastOpType == OpType.NUMBER) {
                            xTokens.add(OperatorToken(Op.MULTIPLY, i1))
                        }

                        if (lastOpType == OpType.RIGHTP) {
                            xTokens.add(OperatorToken(Op.MULTIPLY, i1))
                        }
                        lastOpType = OpType.SYMBOL
                    }

                    in basicOperators -> {
                        if (lastOpType == OpType.OPERATOR) {
                            parseErrors.add(ParseError(MISSING_OPERAND, i1, oper.abbrev()))
                            return false
                        }
                        lastOpType = OpType.OPERATOR
                    }

                    else -> {
                        if (lastOpType == OpType.FUNCTION) {
                            parseErrors.add(ParseError(MISSING_OPERAND, i1, oper.abbrev()))
                            return false
                        }
                        lastOpType = OpType.FUNCTION
                    }
                }
                if (pLevel < 0) {
                    parseErrors.add(ParseError(TOO_MANY_LEFT_PARANTHESIS, i1))
                    return false
                }
                xTokens.add(OperatorToken(oper.op, oper.pos))
                pos.index = i1 + oper.abbrev().length
            }
        } while (pos.index < expr.length)
        if (pLevel > 0) {
            parseErrors.add(ParseError(TOO_MANY_RIGHT_PARANTHESIS, leftpPos))
            return false
        }
        return true
    }

}