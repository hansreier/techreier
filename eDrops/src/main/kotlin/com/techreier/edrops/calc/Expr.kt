package com.techreier.edrops.calc

import com.techreier.edrops.calc.utils.Range
import com.techreier.edrops.calc.utils.checkVariables
import com.techreier.edrops.calc.utils.findClosure
import com.techreier.edrops.calc.utils.findUnboundOperator
import com.techreier.edrops.calc.utils.parseArguments
import com.techreier.edrops.calc.utils.removeOuterP
import com.techreier.edrops.exceptions.ParseException
import org.slf4j.LoggerFactory

class Expr<T>(val calc: Calc<T>, expr: String) {
    protected val logger = LoggerFactory.getLogger(javaClass.simpleName)
    val expr: String = expr.trim()
    val tokens = ArrayList<Token>()
    private lateinit var rawTokens: List<Token>

    //TODO Move to multiline parser later
    //TODO Exception handling is not good enough
    fun setVars(input: String) {
        input.split(";").forEach { assignment ->
            val parts = assignment.split("=")
            if (parts.size == 2) {
                val variableName = parts[0].trim().uppercase()
                val rawValue = parts[1].trim()
                try {
                    val op = Op.valueOf(variableName)
                    this[op] = calc.parseValue(rawValue)
                } catch (_: IllegalArgumentException) { //TODO Add position later?
                    throw ParseException("Cannot parse number", ParseError(VARIABLE_NOT_FOUND, 0, variableName ))
                }
            }
        }
    }

    operator fun set(op: Op, value: T) {
        calc[op] = value
    }

    fun setAll(newVars: Map<Op, T>) {
        calc.setAll(newVars)
    }

    operator fun get(op: Op): T {
        return calc[op]
    }

    fun trace(trace: Trace) {
        calc.trace = trace
    }

    fun result(): T {
        return calc.result()
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
        trace("Expression: $expr")
        trace("checking variables")
        checkVariables(calc)
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
                throw ParseException("Cannot parse more than $MAX_TOKENS tokens on the same level", maxTokensError)
            }
            when (val token = rawTokens[index]) {
                is NumberToken -> {
                    trace("add number: ${token.argument}")
                    tokens.add(token)
                    postphonedOperator?.let { op ->
                        tokens.add(op)
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
                            postphonedOperator?.let { op ->
                                tokens.add(op)
                                trace("add operator: ${(op as OperatorToken).operator.abbrev()}")
                            }
                            postphonedOperator = null
                            index++
                        }

                        token.isLeftP() -> {
                            val argumentInfo = findClosure(Range(index, range.max), rawTokens)
                            recursiveParse(argumentInfo.range, depth + 1)
                            index = argumentInfo.nextPos
                            postphonedOperator?.let { op ->
                                tokens.add(op)
                                trace("add operator: ${(op as OperatorToken).operator.abbrev()}")
                            }
                            postphonedOperator = null
                        }

                        token.isFunction() -> {
                            if (token.isNegate() && ( index < rawTokens.size -1 ) && rawTokens[index + 1].isFunction() ) {
                                val argumentInfo = parseArguments(Range(index + 1, range.max), rawTokens)
                                recursiveParse(Range(argumentInfo.range.min -2, argumentInfo.range.max +1), depth + 1)
                                trace("add func: ${token.operator.abbrev()}")
                                tokens.add(token)
                                index = argumentInfo.nextPos
                            } else {
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
                                index = argumentInfo.nextPos
                            }

                        }

                        token.isOperator() -> {
                            if (index == range.max || index == 0) { // misplaced operator check
                                val posText = if (index == 0) "start" else "end"
                                throw ParseException(
                                    "Misplaced operator at $posText of expression",
                                    ParseError(MISPLACED_OPERATOR, token.position, token.toTokenString())
                                )
                            }

                            if (token.isPow()) { //TODO ReierAsk Exception cases
                                recursiveParse(Range(index + 1, tokens.size), depth + 1)
                                tokens.add(token)
                            }

                            val opInfo = findUnboundOperator(
                                range = Range(index + 1, range.max),
                                prior = token.operator.prior(),
                                tokens = rawTokens
                            )


                            if (!opInfo.push) {
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

        trace("Recur[$depth]: End")
    }

    private fun trace(text: String) {
        if ((calc.trace == Trace.ALL) || calc.trace == Trace.PARSE) {
            logger.info(text)
        }
    }

}