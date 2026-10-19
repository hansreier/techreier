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

    fun parse() {
        logger.info("Preparsing")
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
        var leftRange: Range? = null
        var index = range.min
     //   var leftPIndex = 0
        var lastOpIndex = -1
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
                    tokens.add(token)
                    index++
                }
                is OperatorToken -> {
                    when {
                        token.isLeftP() -> {
                            val argumentInfo = findClosure(Range(index, range.max), rawTokens)
                            logger.info("leftP found")
                            recursiveParse(argumentInfo.range, depth + 1)
                            index = argumentInfo.range.max + 1
                        }

                        token.isFunction() -> {
                            val argumentInfo = parseArguments(Range(index, range.max), rawTokens)

                            if (argumentInfo.push) {
                                logger.info("pushing")
                                tokens.addAll(argumentInfo.tokens)
                                tokens.add(token)
                            } else {
                                logger.info("expression cannot be pushed to stack")
                                recursiveParse(argumentInfo.range, depth + 1)
                                tokens.add(token)
                            }
                            index = argumentInfo.range.max + 1

                        }

                        token.isOperator() -> {
                            if (lastOpIndex > 0) {  // check last operator
                                val lastOpToken = (rawTokens[lastOpIndex] as OperatorToken)
                                if (token.operator.prior() >= lastOpToken.operator.prior()) { // +- 3 */ 2 ^ 1
                                    tokens.add(lastOpToken)
                                }
                            }
                            logger.info("operator found")
                            leftRange = Range(range.min, index - 1)
                            if (index == range.max || index == 0 ) throw ParseException("operator at the end ",
                                ParseError(MISPLACED_OPERATOR, token.position, token.toTokenString()))
                            lastOpIndex = index
                            index++
                        }
                    }
                }
            }
            logger.info("index=$index range.max=${range.max}")
        } while (index < range.max)
        if (lastOpIndex > 0) {  // check last operator
            tokens.add( (rawTokens[lastOpIndex] as OperatorToken))
        }
        logger.info("Rec[$depth]: End")
    }


}