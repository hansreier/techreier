package com.techreier.edrops.calc

import com.techreier.edrops.config.logger
import java.util.*

abstract class Calc<T>(
    val type: Class<T>,
    var logOp: Boolean = false,
) {
    var stack: Deque<T> = ArrayDeque()
    var variables: ArrayList<T> = ArrayList()
    var logStack: Boolean = true
    var operators: EnumSet<Op> = EnumSet.of(
        Op.EMPTY, Op.REMOVE, Op.YX, Op.ADD,
        Op.SUBTRACT, Op.DIVIDE, Op.MULTIPLY, Op.X2, Op.SUM, Op.POW,
        Op.SEPARATOR, Op.X, Op.Y, Op.Z, Op.U, Op.V, Op.W, Op.LEFTP, Op.RIGHTP
    )

    fun saveVarsFromStack() {
        variables.clear()
        variables.addAll(stack)
    }

    fun addVarsOnEmptyStack() {
        stack.clear()
        stack.addAll(variables)
    }

    fun logOp(text: String) {
        if (logOp)
            print(text)
    }

    fun clear() {
        logger.debug("tømmer stakken")
        stack.clear()
    }

    abstract fun enter(number: Number)

    abstract fun enter(number: Double)

    abstract fun enter(number: Int)

    abstract fun enter(number: Long)

    abstract fun enter(number: String)

    protected abstract fun opr(operator: Op, noArgs: Int)


    fun op(operator: Op, number: Number): T? {
        return try {
            print("$number ")
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
                for (i in 1..noArgs) {
                    val t: T = arg.next()

                    if (!type.isInstance(t)) {
                        validElements = false
                        break
                    }
                }

                logOp("${operator.abbrev()} ")
                if (operators.contains(operator)) {
                    if (validElements) {
                        opr(operator, noArgs)
                    } else {
                        logger.error("All arguments is not of the dame data type!")
                        logStack()
                        return null
                    }
                } else {
                    logger.error("Operator ${operator.abbrev()} invalid for ${type.simpleName}.")
                    logStack()
                    return null
                }
                logStack()
                return if (!stack.isEmpty()) {
                    stack.first
                } else {
                    null
                }
            } else {
                logger.error("${operator.abbrev()} : Too few arguments ${stack.size}, must be $minst$noArgs!")
                logStack()
                return null
            }
        } catch (e: Exception) {
            logger.error("Calculation error: ${operator.abbrev()} ${e.message}.")
            logStack()
            return null
        }
    }

    fun result(): T? {
        val i1: T?
        try {
            i1 = stack.peekFirst()
            return if (i1 != null) {
                stack.first
            } else {
                null
            }
        } catch (e: Exception) {
            logger.error("Error when fetching result: ${e.message}.")
            return null
        }
    }

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

    // Log the stack
    fun logStack() {
        val i1: T?
        if (logStack) {
            try {
                i1 = stack.peekFirst()
                if (i1 != null) {
                    logger.info("${stack.first} ")
                    logger.info("( ")
                    val i = stack.iterator()
                    i.next()
                    while (i.hasNext()) {
                        logger.info("${i.next()} ")
                    }
                    logger.info(")")
                } else {
                    logger.info("( )")
                }
            } catch (e: Exception) {
                logger.error("Error when printing stack: ${e.message}.")
            }
        }
    }
}