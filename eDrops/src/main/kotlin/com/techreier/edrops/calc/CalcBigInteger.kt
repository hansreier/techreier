package com.techreier.edrops.calc

import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode
import java.util.ArrayList


class CalcBigInteger : Calc<BigInteger>() {
    override val type: Class<BigInteger> = BigInteger::class.java

   private val vars = Array(Op.entries.size) { BigInteger.ZERO }

    init {
        if (type != BigInteger::class.java) {
            throw IllegalArgumentException("Feil parameter ${type.simpleName} , må være ${BigInteger::class.java.simpleName}.")
        }
    }

    override operator fun set(op: Op, value: BigInteger) {
        vars[op.ordinal] = value
    }

    override operator fun get(op: Op): BigInteger {
        return vars[op.ordinal]
    }

    override fun enter(number: Number) {
        val b = when (number) {
            is BigInteger -> number
            is BigDecimal -> number.setScale(0, RoundingMode.HALF_EVEN).toBigInteger()
            is Double, is Float -> BigDecimal.valueOf(number.toDouble()).setScale(0, RoundingMode.HALF_EVEN).toBigInteger()
            else -> BigInteger.valueOf(number.toLong())
        }
        stack.push(type.cast(b))
        traceStack(number.toString())
    }

    override fun enter(number: String) {
        try {
            val b = BigInteger(number)
            stack.push(type.cast(b))
            traceStack(number)
        } catch (_: NumberFormatException) {
            logger.error("$number : Feil nummer format. ")
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
                val index = operator.ordinal - Op.X.ordinal
                if (index < vars.size) {
                    r.add(vars[index] as BigInteger)
                } else {
                    throw IllegalStateException("has no value")
                }
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