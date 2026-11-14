package com.techreier.edrops.calc

import com.techreier.edrops.calc.Op.Companion.variables
import com.techreier.edrops.util.Trace
import com.techreier.edrops.exceptions.CalcException
import com.techreier.edrops.util.Scope
import com.techreier.edrops.util.Trace.err
import kotlin.reflect.KClass

abstract class Calc<T: Any> {
    abstract val type: KClass<T>
    abstract val vars: MutableMap<Op, T>
    val stack = ArrayDeque<T>()
    val s = Op.entries.size

    //Common operators for all calculators
    var operators = setOf(
        Op.ADD,Op.LOG10, Op.POW10,
        Op.SUBTRACT, Op.DIVIDE, Op.MULTIPLY, Op.SQR, Op.POW,
        Op.SEPARATOR, Op.LEFTP, Op.RIGHTP, Op.NEGATE, Op.ABS, Op.SIGN,
        Op.SUM, Op.MAX, Op.MIN, Op.SELECT, Op.TRUE, Op.FALSE
    ) + variables

    fun clear() {
        stack.clear()
    }

    abstract operator fun set(op: Op, value: T)

    abstract fun parseValue(input: String): T

    abstract fun setAll(newVars: Map<Op, T>)

    abstract operator fun get(op: Op): T

    abstract fun enter(number: Number)

    fun enter(number: String) {
        try {
            val value = parseValue(number)
            stack.addLast(value)
        } finally {
            traceStack(number)
        }
    }

    protected abstract fun opr(operator: Op, noArgs: Int)


    fun operate(operator: Op, noRealArgs: Int = operator.noArgs()): T? {
        val arg: Iterator<T>
        var noArgs = noRealArgs
        try {
            if (noArgs <= -1000) // Allow for RPN direct input of variable number of arguments.
                noArgs = stack.size
            else if (noArgs < 0) {
                noArgs = maxOf(stack.size, -noArgs)
            }

            if (stack.size >= noArgs) {
                arg = stack.iterator()
                var validElements = true
                var remaining = noArgs
                while (remaining > 0) { // check if all arguments are of the same type
                    val t: T = arg.next()
                    if (!type.isInstance(t)) {
                        validElements = false
                        break
                    }
                    remaining--
                }

                if (operators.contains(operator)) {
                    if (validElements) {
                        opr(operator, noArgs)
                    } else {
                        err(Scope.CALC,"All arguments is not of the same data type!")
                        throw CalcException(VARYING_ARGUMENT_TYPES, operator.abbrev())
                    }
                } else {
                    err(Scope.CALC,"Operator ${operator.abbrev()} invalid for ${type.simpleName}.")
                    throw CalcException(INVALID_ARGUMENT, operator.abbrev())
                }
                return if (!stack.isEmpty()) {
                    stack.first()
                } else {
                    null
                }
            } else {
                err(Scope.CALC,"${operator.abbrev()} : Too few arguments ${stack.size}, must be $noArgs! or more")
                throw CalcException(TOO_FEW_ARGUMENTS, operator.abbrev())
            }

        } catch (e: Exception) {
            err(Scope.CALC,"Calculation error: ${operator.abbrev()} ${e.message}.")
            when (e) {
                is CalcException -> {
                    throw CalcException (e.key, e.oper, e.detailMessage)
                }
                is ArithmeticException -> {
                    throw CalcException(CALCULATION_ERROR, operator.abbrev(), e.message)
                }
                is IllegalArgumentException -> {
                    throw CalcException(INVALID_ARGUMENT, operator.abbrev(), e.message)
                }
                else -> {
                    throw e
                }
            }
        }  finally {
            traceStack(operator.abbrev())
        }
    }

    fun result(): T {
        traceStack("Result: ")
        if (stack.isEmpty()) throw CalcException(NO_RESULT, "calc")
        if (stack.size > 1) throw CalcException(MULTIPLE_RESULTS, "calc")
        return stack.first()
    }

    fun allResults(): ArrayList<T> {
        val list = ArrayList(stack)
        list.reverse()
        return list
    }

    // Log the stack
    fun traceStack(operation: String = "") {
            val stack = stack.joinToString(separator = " ", prefix = "[", postfix = "]")
            val paddedOp = operation.padEnd(OPERATOR_TEXT_LOG_SIZE, ' ')
            Trace.trace(Scope.CALC,"$paddedOp$stack")
    }

}