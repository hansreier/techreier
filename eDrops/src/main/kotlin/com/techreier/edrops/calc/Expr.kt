package com.techreier.edrops.calc

import com.techreier.edrops.calc.Op.Companion.variables
import com.techreier.edrops.calc.utils.Range
import com.techreier.edrops.util.Trace
import com.techreier.edrops.calc.utils.checkVariables
import com.techreier.edrops.calc.utils.findClosure
import com.techreier.edrops.calc.utils.findUnboundOperator
import com.techreier.edrops.calc.utils.parseArguments
import com.techreier.edrops.calc.utils.removeOuterP
import com.techreier.edrops.calc.utils.sanitize
import com.techreier.edrops.exceptions.ParseException
import com.techreier.edrops.exceptions.VariableException
import com.techreier.edrops.util.Trace.trace
import com.techreier.edrops.util.TraceScope

class Expr<T : Any>(val calc: Calc<T>, expr: String) {
    val expr: String = expr.sanitize()
    val tokens = ArrayList<Token>()
    var target: Op = Op.X
    var singleVariable: Op? = null
    private lateinit var rawTokens: List<Token>

    operator fun set(op: Op, value: T) {
        calc[op] = value
    }

    fun variable(name: String ): Op? {
        singleVariable =  Op.operator(name, variables)
        return singleVariable
    }

    fun setAll(newVars: Map<Op, T>) {
        calc.setAll(newVars)
    }

    operator fun get(op: Op): T {
        return calc[op]
    }

    fun result(): T {
        return calc.result()
    }

    fun calculate() {
        trace(TraceScope.PARSE, "calculate")
        calc.clear() //clear the stack
        for (t in tokens) {
            when (t) {
                is NumberToken -> calc.enter(t.argument)
                is OperatorToken -> calc.operate(t.operator, t.noArgs)
            }
        }
    }

    fun calc(number: T): T {
        this[singleVariable  ?: throw VariableException("singleVariable","")] = number
        calculate()
        return result()
    }

    fun parse() {
        trace(TraceScope.PARSE,"Expression: $expr")
        trace(TraceScope.PARSE, "checking variables")
        checkVariables(calc)
        trace(TraceScope.PARSE,"Preparsing")
        val result = preparse(calc, expr)
        result.parseError?.let { throw ParseException("preparsing error", it) }
        rawTokens = result.tokens
        trace(TraceScope.PARSE,"Parsing")
        recursiveParse(Range(0, rawTokens.size - 1), 0)
        trace(TraceScope.PARSE,"Parsed")
    }

    fun recursiveParse(rangeIn: Range, depth: Int) {
        if (depth > MAX_EXPRESSION_DEPTH) {
            val token = rawTokens[rangeIn.min]
            val depthError = ParseError(EXPRESSION_DEPTH_EXCEEDED, token.position, token.toTokenString())
            throw ParseException("Expression depth exceeded", depthError)
        }
        var tokenCount = 0
        val range = removeOuterP(rangeIn, rawTokens)
        if (range.max >= range.min) {
            var index = range.min
            var postphonedOperator: Token? = null
            trace(TraceScope.PARSE,"Recur[$depth] ${rawTokens.toExpr(range)}")
            do {
                tokenCount++
                if (tokenCount > MAX_TOKENS) {
                    val token = rawTokens[index]
                    val maxTokensError = ParseError(MAX_TOKENS_EXCEEDED, token.position, token.toTokenString())
                    throw ParseException("Cannot parse more than $MAX_TOKENS tokens on the same level", maxTokensError)
                }
                when (val token = rawTokens[index]) {
                    is NumberToken -> {
                        trace(TraceScope.PARSE,"add number: ${token.argument}")
                        tokens.add(token)
                        postphonedOperator?.let { op ->
                            tokens.add(op)
                            trace(TraceScope.PARSE,"add operator: ${(op as OperatorToken).operator.abbrev()}")
                        }
                        postphonedOperator = null
                        index++
                    }

                    is OperatorToken -> {
                        when {
                            token.isSymbol() -> {
                                val typeName = if (token.isVariable()) "variable" else "constant"
                                trace(TraceScope.PARSE,"add $typeName: ${token.operator.abbrev()}")
                                tokens.add(token)
                                postphonedOperator?.let { op ->
                                    tokens.add(op)
                                    trace(TraceScope.PARSE,"add operator: ${(op as OperatorToken).operator.abbrev()}")
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
                                    trace(TraceScope.PARSE,"add operator: ${(op as OperatorToken).operator.abbrev()}")
                                }
                                postphonedOperator = null
                            }

                            token.isFunction() -> {
                                if ((token.isNegate() || token.isNot() )&& (index < rawTokens.size - 1) && rawTokens[index + 1].isFunction()) {
                                    val argumentInfo = parseArguments(Range(index + 1, range.max), rawTokens)
                                    recursiveParse(
                                        Range(argumentInfo.range.min - 2, argumentInfo.range.max + 1),
                                        depth + 1
                                    )
                                    trace(TraceScope.PARSE, "add func: ${token.operator.abbrev()}")
                                    tokens.add(token)
                                    index = argumentInfo.nextPos
                                } else {
                                    val argumentInfo = parseArguments(Range(index, range.max), rawTokens)
                                    if (argumentInfo.push) {
                                        trace(TraceScope.PARSE,"add args:${argumentInfo.tokens.toTokenString()}")
                                        tokens.addAll(argumentInfo.tokens)
                                        trace(TraceScope.PARSE,"add func: ${token.operator.abbrev()}")
                                        tokens.add(token)
                                    } else {
                                        recursiveParse(argumentInfo.range, depth + 1)
                                        trace(TraceScope.PARSE,"add func: ${token.operator.abbrev()}")
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

                                val opInfo = findUnboundOperator(
                                    range = Range(index + 1, range.max),
                                    prior = token.operator.prior(),
                                    tokens = rawTokens
                                )

                                if (!opInfo.push) {
                                    recursiveParse(Range(index + 1, opInfo.endPos), depth + 1)
                                    trace(TraceScope.PARSE,"add operator: ${token.operator.abbrev()}")
                                    if (!token.isSeparator()) tokens.add(token)
                                    index = opInfo.endPos + 1
                                } else {
                                    if (token.isPow()) {
                                        if (index < rawTokens.size) {
                                            recursiveParse(Range(index + 1, opInfo.endPos), depth + 1)
                                            trace(TraceScope.PARSE,"add operator: ${token.operator.abbrev()}")
                                            tokens.add(token)
                                            index++
                                        }
                                    } else {
                                        if (!token.isSeparator()) postphonedOperator = token
                                    }
                                    index++
                                }

                            }
                            else -> {
                                throw ParseException("Operator mismatch, check lists in Calc and Op ",
                                    ParseError(OPERATOR_MISMATCH, token.position, token.operator.abbrev()))
                            }
                        }
                    }
                }
            } while (index <= range.max)
        } else {
            Trace.warn(TraceScope.PARSE,"Calling recursive parse without token that can be calculated")
        }
        trace(TraceScope.PARSE,"Recur[$depth]: End")
    }

}