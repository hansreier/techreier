package com.techreier.edrops.calc

import com.techreier.edrops.exceptions.CalcException
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode
import java.util.ArrayList
import java.util.EnumMap
import kotlin.collections.set


class CalcBigInteger : Calc<BigInteger>() {
    override val type: Class<BigInteger> = BigInteger::class.java

    private val vars: MutableMap<Op, BigInteger> = EnumMap(Op::class.java)


    init {
        if (type != BigInteger::class.java) {
            throw IllegalArgumentException("Feil parameter ${type.simpleName} , må være ${BigInteger::class.java.simpleName}.")
        }
    }

    override operator fun set(op: Op, value: BigInteger) {
        vars[op] = value
    }

    override operator fun get(op: Op): BigInteger {
        val variable = vars[op] ?: throw IllegalArgumentException("No variable ${op.abbrev()}")
        return variable
    }

    override fun enter(number: Number) {
        try {
            val b = when (number) {
                is BigInteger -> number
                is BigDecimal -> number.setScale(0, RoundingMode.HALF_EVEN).toBigInteger()
                is Double -> BigDecimal.valueOf(number)
                    .setScale(0, RoundingMode.HALF_EVEN).toBigInteger()
                else -> BigInteger.valueOf(number.toLong())
            }
            stack.push(b)
        } catch (_: NumberFormatException) {
            throw CalcException(NUMBER_FORMAT_ERROR, number.toString())
        } finally {
            traceStack(number.toString())
        }
    }

    override fun enter(number: String) {
        try {
            val b = BigInteger(number)
            stack.push(b)
        } catch (_: NumberFormatException) {
            throw CalcException(NUMBER_FORMAT_ERROR, number)
        } finally {
            traceStack(number)
        }
    }

    override fun opr(operator: Op, noArgs: Int) {
        val arg = stack.iterator()
        val r = ArrayList<BigInteger>()

        val x: BigInteger
        val y: BigInteger

        when (operator) {
            Op.ADD -> {
                y = arg.next() as BigInteger
                x = arg.next() as BigInteger
                r.add(x.add(y))
            }
            Op.SUBTRACT -> {
                y = arg.next() as BigInteger
                x = arg.next() as BigInteger
                r.add(x.subtract(y))
            }
            Op.MULTIPLY -> {
                y = arg.next() as BigInteger
                x = arg.next() as BigInteger
                r.add(x.multiply(y))
            }
            Op.DIVIDE -> {
                y = arg.next() as BigInteger
                x = arg.next() as BigInteger
                r.add(x.divide(y))
            }
            Op.X2 -> {
                x = arg.next() as BigInteger
                r.add(x.multiply(x))
            }
            Op.POW -> {
                y = arg.next() as BigInteger
                x = arg.next() as BigInteger
                r.add(x.pow(y.intValueExact()))
            }
            Op.SUM -> {
                r.add(BigInteger.ZERO)
               repeat (noArgs) {
                    val x = arg.next() as BigInteger
                    r[0] = r[0].add(x)
                }
            }
            Op.REMOVE, Op.EMPTY -> {}
            Op.YX -> {
                r.add(arg.next() as BigInteger)
                r.add(arg.next() as BigInteger)
            }
            Op.SEPARATOR -> { //Separator really does nothing but to be removed
                val x = arg.next() as BigInteger
                r.add(arg.next() as BigInteger)
                r.add(x)
            }
            Op.X, Op.Y, Op.Z, Op.U, Op.V, Op.W -> {
                val variable = vars[operator]
                variable ?: throw (IllegalArgumentException("Unknown variable: ${operator.abbrev()}"))
                r.add(variable)
            }
            else -> {
                logger.error("Implementation of ${operator.abbrev()} forgotten for ${type.simpleName}")
            }
        }

        repeat (noArgs) {
            stack.poll()
        }

        for (res in r) {
            stack.push(type.cast(res))
        }
    }
}