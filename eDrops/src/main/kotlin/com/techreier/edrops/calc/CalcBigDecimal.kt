package com.techreier.edrops.calc

import com.techreier.edrops.exceptions.CalcException
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode
import java.util.ArrayList
import java.util.EnumMap


class CalcBigDecimal : Calc<BigDecimal>() {
    override val type: Class<BigDecimal> = BigDecimal::class.java

    override val vars: MutableMap<Op, BigDecimal> = EnumMap(Op::class.java)

    var scale: Int = 20

    init {
        if (type != BigDecimal::class.java) {
            throw IllegalArgumentException("Feil parameter ${type.simpleName} , må være ${BigDecimal::class.java.simpleName}.")
        }
    }

    override operator fun set(op: Op, value: BigDecimal) {
        vars[op] = value
    }

    override operator fun get(op: Op): BigDecimal {
        val variable = vars[op] ?: throw IllegalArgumentException("No variable ${op.abbrev()}")
        return variable
    }

    override fun enter(number: Number) {
        try {
            val b = when (number) {
                is BigDecimal -> number
                is BigInteger -> BigDecimal(number)
                else -> BigDecimal(number.toString())
            }
            stack.push(b)
        } catch (_:NumberFormatException) {
            throw CalcException(NUMBER_FORMAT_ERROR, number.toString())
        } finally {
        traceStack(number.toString())
    }
    }

    override fun enter(number: String) {
        try {
            val b = BigDecimal(number)
            stack.push(b)
        } catch (_: NumberFormatException) {
            logger.error("$number : Feil nummer format. ")
            throw CalcException(NUMBER_FORMAT_ERROR, number)
        } finally {
            traceStack(number)
        }
    }

    override fun opr(operator: Op, noArgs: Int) {
        val arg = stack.iterator()
        val r = ArrayList<BigDecimal>()

        when (operator) {
            Op.ADD -> {
                val y = arg.next() as BigDecimal
                val x = arg.next() as BigDecimal
                r.add(x.add(y))
            }
            Op.SUBTRACT -> {
                val y = arg.next() as BigDecimal
                val x = arg.next() as BigDecimal
                r.add(x.subtract(y))
            }
            Op.MULTIPLY -> {
                val y = arg.next() as BigDecimal
                val x = arg.next() as BigDecimal
                r.add(x.multiply(y))
            }
            Op.DIVIDE -> {
                val y = arg.next() as BigDecimal
                val x = arg.next() as BigDecimal
                r.add(x.divide(y, scale, RoundingMode.HALF_UP))
            }
            Op.X2 -> {
                val x = arg.next() as BigDecimal
                r.add(x.multiply(x))
            }
            Op.POW -> {
                val y = arg.next() as BigDecimal
                val x = arg.next() as BigDecimal
                val rd: BigDecimal
                val yInt = y.toInt()

                if (yInt >= 0) {
                    rd = x.pow(yInt)
                } else {
                    val aa = x.pow(-yInt)
                    rd = BigDecimal.ONE.divide(aa, scale, RoundingMode.HALF_UP)
                }
                val rm = y.remainder(BigDecimal.ONE)
                val finalRd = if (rm.compareTo(BigDecimal.ZERO) != 0) {
                    rd.multiply(BigDecimal.valueOf(Math.pow(x.toDouble(), rm.toDouble())))
                } else {
                    rd
                }
                r.add(finalRd)
            }
            Op.SUM -> {
                var sum = BigDecimal.ZERO
                repeat(noArgs) {
                    val x = arg.next() as BigDecimal
                    sum = sum.add(x)
                }
                r.add(sum)
            }
            Op.REMOVE, Op.EMPTY -> {}
            Op.YX -> {
                r.add(arg.next() as BigDecimal)
                r.add(arg.next() as BigDecimal)
            }
            Op.SEPARATOR -> { //Separator really does nothing but to be removed
                val x = arg.next() as BigDecimal
                r.add(arg.next() as BigDecimal)
                r.add(x)
            }
            Op.X, Op.Y, Op.Z, Op.U, Op.V, Op.W -> {
                val variable = vars[operator]
                variable ?: throw (IllegalArgumentException("Unknown variable: ${operator.abbrev()}"))
                r.add(variable)
            }
            else -> {
                logger.error("Implementation of ${operator.abbrev()} forgotten for ${type.simpleName}.")
            }
        }

        repeat(noArgs) {
            stack.poll()
        }

        for (res in r) {
            stack.push(type.cast(res))
        }
    }
}