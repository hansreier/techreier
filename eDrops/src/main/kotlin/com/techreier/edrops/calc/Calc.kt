package com.techreier.edrops.calc

import org.slf4j.LoggerFactory
import java.util.*

abstract class Calc<T>() {
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


    fun op(operator: Op, number: Number): T? {
        return try {
            stack.push(type.cast(number))
            op(operator)
        } catch (e: Exception) {
            logger.error("Error ${operator.abbrev()}: ${e.message}.")
            null
        }
    }


    fun op(operator: Op): T? {
        val arg: Iterator<T>
        var noArgs: Int
        var minst = ""
        try {
            noArgs = operator.noArgs()

            if (noArgs <= -1000)
                noArgs = stack.size
            else if (noArgs < 0) {
                minst = "minst "
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

             //   traceStack(operator.abbrev())

                if (operators.contains(operator)) {
                    if (validElements) {
                        opr(operator, noArgs)
                    } else {
                        logger.error("All arguments is not of the same data type!")
                        traceStack(operator.abbrev())
                        return null
                    }
                } else {
                    logger.error("Operator ${operator.abbrev()} invalid for ${type.simpleName}.")
                    traceStack(operator.abbrev())
                    return null
                }
                traceStack(operator.abbrev())
                return if (!stack.isEmpty()) {
                    stack.first
                } else {
                    null
                }
            } else {
                logger.error("${operator.abbrev()} : Too few arguments ${stack.size}, must be $minst$noArgs!")
                traceStack(operator.abbrev())
                return null
            }
        } catch (e: Exception) {
            logger.error("Calculation error: ${operator.abbrev()} ${e.message}.")
            traceStack(operator.abbrev())
            return null
        }
    }

    fun result(): T? = stack.peekFirst()

    fun allResults(): ArrayList<T> {
        try {
            val list = ArrayList(stack)
            Collections.reverse(list)
            return list
        } catch (e: java.lang.Exception) {
            logger.error("Error when collecting all results: ${e.message}.")
            return ArrayList<T>()
        }
    }

    fun traceText(text: String) {
        if (trace == Trace.ALL) {
            logger.info(text)
        }
    }

    // Log the stack
    fun traceStack(operation: String = "") {
        if ((trace == Trace.ALL) || trace == Trace.RPN) {
            val stack = if (trace == Trace.ALL)  stack.joinToString(separator = " ", prefix = "[", postfix = "]") else ""
            val paddedOp = operation.padEnd(OPERATOR_TEXT_LOG_SIZE, ' ')
            logger.info("$paddedOp$stack")
        }
    }
}