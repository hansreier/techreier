package com.techreier.edrops.calc

import com.techreier.edrops.config.logger
import java.util.*

const val MISSING_OPERATOR = "MissingOperator"
const val UNPARSEABLE = "Unparseable"
const val EMPTY_PARENTHESIS = "EmptyParenthesis"
const val MISPLACED_PARENTHESIS = "MisplacedParenthesis"
const val TOO_MANY_LEFT_PARENTHESIS = "TooManyLeftParenthesis"
const val TOO_MANY_RIGHT_PARENTHESIS = "TooManyRightParenthesis"
const val MISPLACED_OPERATOR = "MisplacedOperator"
const val MISPLACED_SEPARATOR = "MisplacedSeparator"
const val MISSING_OPERAND = "MissingOperand"
const val CONSECUTIVE_FUNCTIONS = "ConsecutiveFunctions"
const val MISPLACED_FUNCTION = "MisplacedFunction"
const val FUNCTION_NO_ARGUMENTS = "FuncNoArguments"
const val WRONG_NO_OF_ARGUMENTS = "WrongNoOfArguments"
const val SEPARATOR_NOT_IN_FUNCTION = "SeparatorNotInFunction"

class Expr(val calc: Calc<*>, expr: String) {

    val expr: String
    var parseError: ParseError? = null
    val tokens = ArrayList<Token>()
    val xTokens = ArrayList<Token>()
    private var resultSize = 0
    private var trace: Trace = Trace.OFF
    private var opStack = ArrayDeque<Oper>()
    private var noOfResults = 1

    init {
        this.expr = expr.trim()
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

    fun errorIndicator(): String? {
        val errPosition = this.parseError?.position ?: return null
        val startPos = (errPosition - 40).coerceIn(0, expr.length)
        val endPos = (errPosition + 40).coerceIn(0, expr.length)
        return "${expr.substring(startPos, errPosition)}???" +
                "${if (errPosition == expr.length) "" else expr.substring(errPosition, endPos)}"
    }

    fun errorText(): String? {
        val parseError = this.parseError ?: return null
        return "${parseError.key}: ${errorIndicator()}"
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
                parseError = ParseError(WRONG_NO_OF_ARGUMENTS, o.pos, o.abbrev())
                return false
            }
        }

        resultSize += -o.op.noArgs() + o.op.noResults()
        tokens.add(OperatorToken(o.op, o.pos))
        trace("@adding token:$o level: $resultSize)")
        return true
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
    fun preparseWrapper(): Boolean {
        val preparseResult = preparse(calc, expr)
        parseError = preparseResult.parseError
        xTokens.addAll(preparseResult.tokens)
       if (preparseResult.parseError != null) return false else return true
    }

}