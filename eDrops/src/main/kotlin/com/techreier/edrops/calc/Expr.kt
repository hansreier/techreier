package com.techreier.edrops.calc

import com.techreier.edrops.config.logger
import com.techreier.edrops.exceptions.ParseException

class Expr(val calc: Calc<*>, expr: String) {
    val trace = calc.trace
    val expr: String
    val tokens = ArrayList<Token>()
    private lateinit var rawTokens: List<Token>

    init {
        this.expr = expr.trim()
        trace("Expression: $expr")
    }

    fun calculate() {
            trace("calculate")
            calc.clear() //clear the stack
            for (t in tokens) {
                when (t) {
                    is NumberToken -> calc.enter(t.argument)
                    is OperatorToken -> calc.op(t.operator)
                }
            }
    }

    fun parse() {
        trace("Preparsing")
        val result = preparse(calc, expr)
        result.parseError?.let { throw ParseException("preparsing error", it) }
        rawTokens = result.tokens
        trace("Parsing")
        recursiveParse(Range(0, rawTokens.size - 1), 0)
        trace("Parsed")
    }

    fun recursiveParse(rangeIn: Range, depth: Int) {
        if (depth > MAX_EXPRESSION_DEPTH) {
            val token = rawTokens[rangeIn.min]
            val depthError = ParseError(EXPRESSION_DEPTH_EXCEEDED, token.position, token.toTokenString())
            throw ParseException("Expression depth exceeded", depthError)
        }
        var tokenCount = 0
        val range = removeOuterP(rangeIn, rawTokens)
        var index = range.min
        var postphonedOperator: Token? = null
        trace("Recur[$depth] ${rawTokens.toExpr(range)}")
        do {
            tokenCount++
            if (tokenCount > MAX_TOKENS) { //TODO ReierAsk, check in preparse, test relevant during development
                val token = rawTokens[index]
                val maxTokensError = ParseError(MAX_TOKENS_EXCEEDED, token.position, token.toTokenString())
                throw ParseException("Cannot parse more than $MAX_TOKENS tokens", maxTokensError)
            }
            when (val token = rawTokens[index]) {
                is NumberToken -> {
                    trace("add number: ${token.argument}")
                    tokens.add(token)
                    postphonedOperator?.let {
                        op -> tokens.add(op)
                        trace("add operator: ${(op as OperatorToken).operator.abbrev()}")
                    }
                    postphonedOperator = null
                    index++
                }
                is OperatorToken -> {
                    when {
                        token.isVariable() -> {
                            trace("add variable: ${token.operator.abbrev()}")
                            tokens.add(token)
                            postphonedOperator?.let {
                                op -> tokens.add(op)
                                trace("add operator: $op")
                            }
                            postphonedOperator = null
                            index++
                        }
                        token.isLeftP() -> {
                            val argumentInfo = findClosure(Range(index, range.max), rawTokens)
                            recursiveParse(argumentInfo.range, depth + 1)
                            index = argumentInfo.range.max + 1
                            postphonedOperator?.let {
                                    op -> tokens.add(op)
                                trace("add operator: ${(op as OperatorToken).operator.abbrev()}")
                            }
                            postphonedOperator = null
                        }

                        token.isFunction() -> {
                            val argumentInfo = parseArguments(Range(index, range.max), rawTokens)

                            if (argumentInfo.push) {
                                trace("add args:${argumentInfo.tokens.toTokenString()}")
                                tokens.addAll(argumentInfo.tokens)
                                trace("add func: ${token.operator.abbrev()}")
                                tokens.add(token)
                            } else {
                                recursiveParse(argumentInfo.range, depth + 1)
                                trace("add func: ${token.operator.abbrev()}")
                                tokens.add(token)
                            }
                            index = argumentInfo.range.max + 2

                        }

                        token.isOperator() -> {
                            if (index == range.max || index == 0 ) { // misplaced operator check
                                val posText = if (index == 0) "start" else "end"
                                throw ParseException(
                                    "Misplaced operator at $posText of expression",
                                    ParseError(MISPLACED_OPERATOR, token.position, token.toTokenString())
                                )
                            }

                            val opInfo = findUnboundOperator(
                                range = Range(index + 1, range.max),
                                prior =  token.operator.prior(),
                                tokens = rawTokens
                            )
                            if (opInfo.push) {
                                recursiveParse(Range(index + 1, opInfo.endPos), depth + 1)
                                tokens.add(token)
                                index = opInfo.endPos + 1
                            } else {
                                postphonedOperator = token
                                index += 1
                            }

                        }
                    }
                }
            }
        } while (index <= range.max)

        postphonedOperator?.let { op -> tokens.add(op) }

        trace("Recur[$depth]: End")
    }

    private fun trace(text: String) {
        if (trace != Trace.OFF)   {
            logger.info(text)
        }
    }


}