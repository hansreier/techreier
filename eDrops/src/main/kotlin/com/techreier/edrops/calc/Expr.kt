package com.techreier.edrops.calc

import com.techreier.edrops.config.logger
import com.techreier.edrops.exceptions.ParseException

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
            logger.info("calculate")
            calc.clear() //clear the stack
            for (t in tokens) {
                when (t) {
                    is NumberToken -> calc.enter(t.argument)
                    is OperatorToken -> calc.op(t.operator)
                }
            }
    }

    fun parse() {
        val result = preparse(calc, expr)
        result.parseError?.let { throw ParseException("preparsing error", it) }
        rawTokens = result.tokens
        logger.info("Parsing")
        recursiveParse(Range(0, rawTokens.size - 1), 0)
        logger.info("Parsed")
    }

    fun recursiveParse(rangeIn: Range, depth: Int) {
        if (depth > MAX_EXPRESSION_DEPTH) {
            val token = rawTokens[rangeIn.min]
            val depthError = ParseError(EXPRESSION_DEPTH_EXCEEDED, token.position, token.toTokenString())
            throw ParseException("Expression depth exceeded", depthError)
        }
        var tokenCount = 0
        val range = removeOuterP(rangeIn, rawTokens)
        //     logger.info("Rec[$depth]: $range ${rawTokens.toExpr(range)}")
        var index = range.min
        var postphonedOperator: Token? = null

        do {
            logger.info("Rec[$depth]=$range index=$index ${rawTokens.toExpr(range)}")
            tokenCount++
            if (tokenCount > MAX_TOKENS) { //TODO ReierAsk, check in preparse, test relevant during development
                val token = rawTokens[index]
                val maxTokensError = ParseError(MAX_TOKENS_EXCEEDED, token.position, token.toTokenString())
                throw ParseException("Cannot parse more than $MAX_TOKENS tokens", maxTokensError)
            }
            when (val token = rawTokens[index]) {
                is NumberToken -> {
                    calc.traceText("add number: $token")
                    tokens.add(token)
                    postphonedOperator?.let {
                        op -> tokens.add(op)
                        calc.traceText("add operator: $op")
                    }
                    postphonedOperator = null
                    index++
                }
                is OperatorToken -> {
                    when {
                        token.isVariable() -> {
                            calc.traceText("add variable: $token")
                            tokens.add(token)
                            postphonedOperator?.let {
                                op -> tokens.add(op)
                                calc.traceText("add operator: $op")
                            }
                            postphonedOperator = null
                            index++
                        }
                        token.isLeftP() -> {
                            val argumentInfo = findClosure(Range(index, range.max), rawTokens)
                            logger.info("leftP found")
                            recursiveParse(argumentInfo.range, depth + 1)
                            index = argumentInfo.range.max + 2
                        }

                        token.isFunction() -> {
                            val argumentInfo = parseArguments(Range(index, range.max), rawTokens)

                            if (argumentInfo.push) {
                                logger.info("pushing")
                                calc.traceText("add arguments: ${argumentInfo.tokens}")
                                tokens.addAll(argumentInfo.tokens)
                                calc.traceText("add function token: $token")
                                tokens.add(token)
                            } else {
                                logger.info("expression cannot be pushed to stack")
                                recursiveParse(argumentInfo.range, depth + 1)
                                calc.traceText("add function: $token")
                                tokens.add(token)
                            }
                            index = argumentInfo.range.max + 2

                        }

                        token.isOperator() -> {
                            logger.info("operator found")
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
            logger.info("index=$index range.max=${range.max}")
        } while (index <= range.max)

        postphonedOperator?.let { op -> tokens.add(op) }

        logger.info("Rec[$depth]: End")
    }


}