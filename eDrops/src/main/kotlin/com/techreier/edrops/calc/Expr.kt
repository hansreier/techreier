package com.techreier.edrops.calc

import com.techreier.edrops.config.logger
import java.util.*

class Expr(val calc: Calc<*>, expr: String) {

    val expr: String
    var parseError: ParseError? = null
    val tokens = ArrayList<Token>()
    private lateinit var rawTokens: List<Token>
    private var resultSize = 0
    private var trace: Trace = Trace.OFF
    private var opStack = ArrayDeque<Oper>()
    private var noOfResults = 1

    init {
        this.expr = expr.trim()
        calc.logOp = false
        trace(expr)
    }

    // TODO Move of delete
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

    /*
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
    }*/

    fun calculate() {
        try {
            logger.info("calculating")
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

    fun recursiveParse(range: Range): Boolean {
        logger.info(("RecursiveParse: $range"))
        val range = removeOuterP(range, rawTokens)
        var calcStart = range.min
        var calcEnd = range.min

        var index = range.min
        do {
            when (val token = rawTokens[index]) {
                is NumberToken -> index++
                is OperatorToken -> {
                    when {
                        token.isFunction() -> {
                            val argumentInfo = parseArguments(Range(index, range.max), rawTokens)
                            parseError = argumentInfo.error
                            if (parseError == null) {
                                if (argumentInfo.push) {
                                    tokens.addAll(argumentInfo.tokens)
                                    tokens.add(token)
                                    index = argumentInfo.range.max + 1
                                } else {
                                    recursiveParse(argumentInfo.range)
                                }
                                index = argumentInfo.range.max + 1
                            }
                        }

                        token.isLeftP() -> {
                            val argumentInfo = findClosure(Range(index, range.max), rawTokens)
                            parseError = argumentInfo.error
                            if (parseError == null) {
                                recursiveParse(argumentInfo.range)
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