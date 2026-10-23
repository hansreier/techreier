package com.techreier.edrops.calc

import com.techreier.edrops.exceptions.CalcException
import org.slf4j.LoggerFactory
import java.util.*

abstract class Calc<T> {
    protected val logger = LoggerFactory.getLogger(javaClass.simpleName)
    abstract val type: Class<T>
    var stack: Deque<T> = ArrayDeque()
    val s = Op.entries.size

    var trace: Trace = Trace.OFF
    var operators: EnumSet<Op> = EnumSet.of(
        Op.EMPTY, Op.REMOVE, Op.YX, Op.ADD,
        Op.SUBTRACT, Op.DIVIDE, Op.MULTIPLY, Op.X2, Op.SUM, Op.POW,
        Op.SEPARATOR, Op.X, Op.Y, Op.Z, Op.U, Op.V, Op.W, Op.LEFTP, Op.RIGHTP
    )

    fun clear() {
        logger.debug("tømmer stakken")
        stack.clear()
    }

    abstract operator fun set(op: Op, value: T)

    abstract operator fun get(op: Op): T

    abstract fun enter(number: Number)

    abstract fun enter(number: String)  //Used for future GUI

    protected abstract fun opr(operator: Op, noArgs: Int)


    fun op(operator: Op): T? {
        val arg: Iterator<T>
        var noArgs: Int
        var atLeast = ""
        try {
            noArgs = operator.noArgs()

            if (noArgs <= -1000)
                noArgs = stack.size
            else if (noArgs < 0) {
                atLeast = "at least "
                noArgs = maxOf(stack.size, -noArgs)
            }

            if (stack.size >= noArgs) {
                arg = stack.iterator()
                var validElements = true
                var remaining = noArgs
                while (remaining > 0) {
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
                        logger.error("All arguments is not of the same data type!")
                        throw CalcException(VARYING_ARGUMENT_TYPES, operator.abbrev())
                    }
                } else {
                    logger.error("Operator ${operator.abbrev()} invalid for ${type.simpleName}.")
                    throw CalcException(INVALID_ARGUMENT, operator.abbrev())
                }
                return if (!stack.isEmpty()) {
                    stack.first
                } else {
                    null
                }
            } else {
                logger.error("${operator.abbrev()} : Too few arguments ${stack.size}, must be $atLeast$noArgs!")
                throw CalcException(TOO_FEW_ARGUMENTS, operator.abbrev())
            }
        } catch (e: Exception) {
            logger.error("Calculation error: ${operator.abbrev()} ${e.message}.")
            throw CalcException(CALCULATION_ERROR, operator.abbrev(), e)
        } finally {
            traceStack(operator.abbrev())
        }
    }

    fun result(): T {
        traceStack("Result: ", true)
        if (stack.isEmpty()) throw CalcException(NO_RESULT, "calc")
        if (stack.size >1) throw
            CalcException(MULTIPLE_RESULTS, "calc")
        return stack.peekFirst()
    }

    fun allResults(): ArrayList<T> {
        val list = ArrayList(stack)
        list.reverse()
        return list
    }

    // Log the stack
    fun traceStack(operation: String = "", forced: Boolean = false) {
        if ((trace == Trace.ALL) || (trace == Trace.RPN) || (forced && (trace != Trace.OFF))) {
            val stack = stack.joinToString(separator = " ", prefix = "[", postfix = "]")
            val paddedOp = operation.padEnd(OPERATOR_TEXT_LOG_SIZE, ' ')
            logger.info("$paddedOp$stack")
        }
    }

}