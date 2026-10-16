package com.techreier.edrops.calc

import com.techreier.edrops.config.logger
import java.util.*

class Expr(val calc: Calc<*>, expr: String) {

    val expr: String
    var parseError: ParseError? = null
    val tokens = ArrayList<Token>()
    private lateinit var rawTokens: List<Token>

    init {
        this.expr = expr.trim()
        calc.traceText(expr)
    }


    private fun warn(text: String, relaxed: Boolean = false) {
        if (relaxed)
            logger.warn("warning: $text")
        else
            logger.error("error: $text")
    }

    fun calculate() {
        try {
            logger.info("calculate")
            calc.clear() //clear the stack
            for (t in tokens) {
                when (t) {
                    is NumberToken -> calc.enter(t.argument)
                    is OperatorToken -> calc.op(t.operator)
                 //   is OperatorToken -> if (calc.op(t.operator) == null) {
                //calc.addVarsOnEmptyStack()
                 //                    break
                    //               }
                }
            }
        } catch (e: Exception) {
            logger.error("Error during calculation: ${e.message}")
        }
    }

    fun parse(): Boolean {
        logger.info("Preparsing")
        val result = preparse(calc, expr)
        parseError = result.parseError
        if (result.parseError != null) return false
        rawTokens = result.tokens
        logger.info("Parsing")
        val parsed =  recursiveParse(Range(0, rawTokens.size - 1))
        logger.info("Parsed")
        return parsed
    }

    fun recursiveParse(rangeIn: Range): Boolean {
        val range = removeOuterP(rangeIn, rawTokens)
        logger.info("Recursive: $range ${rawTokens.toExpr(range)}")

        var index = range.min
        do {
            when (val token = rawTokens[index]) {
                is NumberToken -> index++
                is OperatorToken -> {
                    when {
                        token.isLeftP() -> {
                            val argumentInfo = findClosure(Range(index, range.max), rawTokens)
                            logger.info("leftP found")
                            parseError = argumentInfo.error
                            if (parseError == null) {
                                recursiveParse(argumentInfo.range)
                                index = argumentInfo.range.max + 1
                            }
                        }

                        token.isFunction() -> {
                            val argumentInfo = parseArguments(Range(index, range.max), rawTokens)
                            parseError = argumentInfo.error
                            if (parseError == null) {
                                if (argumentInfo.push) {
                                    logger.info("pushing")
                                    tokens.addAll(argumentInfo.tokens)
                                    tokens.add(token)
                                } else {
                                    logger.info("expression cannot be pushed to stack")
                                    recursiveParse(argumentInfo.range)
                                    tokens.add(token)
                                }
                                index = argumentInfo.range.max + 1
                            } else {
                                logger.info("parse error: ${parseError?.key}")
                                return false
                            }
                        }
                    }
                }
            }
            if (parseError != null) return false
            logger.info("index=$index range.max=${range.max}")
        } while (index <= range.max)
        return true
    }


}