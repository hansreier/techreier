package com.techreier.edrops.calc

import java.util.ArrayDeque
import java.util.ArrayList
import java.util.Deque
import java.util.EnumSet

abstract class Calc<T>(
    val type: Class<T>,
    var logOp: Boolean = false
) {
    var stack: Deque<T> = ArrayDeque()
    var variables: ArrayList<T> = ArrayList()
    var logStack: Boolean = true
    var operators: EnumSet<Op> = EnumSet.of(
        Op.EMPTY, Op.REMOVE, Op.YX, Op.ADD,
        Op.SUBTRACT, Op.DIVIDE, Op.MULTIPLY, Op.X2, Op.SUM, Op.POW,
        Op.X, Op.Y, Op.Z, Op.U, Op.V, Op.W
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
        println("tømmer stakken")
        stack.clear()
    }

    abstract fun enter(number: Number)

    abstract fun enter(number: Double)

    abstract fun enter(number: Int)

    abstract fun enter(number: Long)

    abstract fun enter(number: String)

    protected abstract fun op(operator: Op, number: String): T?

    protected abstract fun opr(operator: Op, noArgs: Int)


    fun op(operator: Op, number: Number): T? {
        return try {
            print("$number ")
            stack.push(type.cast(number))
            op(operator)
        } catch (e: Exception) {
            println("Feil ${operator.abbrev()}: ${e.message}.")
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
                        println("Alle argumentene er ikke av samme datatype!")
                        logStack()
                        return null
                    }
                } else {
                    println("Operator ${operator.abbrev()} ugyldig for ${type.simpleName}.")
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
                println("${operator.abbrev()} : For få argumenter ${stack.size}, skal være $minst$noArgs!")
                logStack()
                return null
            }
        } catch (e: Exception) {
            println("Feil ${operator.abbrev()}: ${e.message}.")
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
                println("Ingen verdi!")
                null
            }
        } catch (e: Exception) {
            println("Feil ved resultuthenting: ${e.message}.")
            return null
        }
    }

    // Log the stack
    fun logStack() {
        val i1: T?
        if (logStack) {
            try {
                i1 = stack.peekFirst()
                if (i1 != null) {
                    print("${stack.first} ")
                    print("( ")
                    val i = stack.iterator()
                    i.next()
                    while (i.hasNext()) {
                        print("${i.next()} ")
                    }
                    println(")")
                } else {
                    println("( )")
                }
            } catch (e: Exception) {
                println("Feil ved utskrift av stakk: ${e.message}.")
            }
        }
    }
}