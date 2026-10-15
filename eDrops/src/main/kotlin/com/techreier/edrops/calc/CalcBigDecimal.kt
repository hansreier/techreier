package com.techreier.edrops.calc

import com.techreier.edrops.config.logger
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.ArrayList

class CalcBigDecimal<T>(
    type: Class<T>,
) : Calc<T>(type) {

    var scale: Int = 20

    init {
        if (type != BigDecimal::class.java) {
            throw IllegalArgumentException("Feil parameter ${type.simpleName} , må være ${BigDecimal::class.java.simpleName}.")
        }
    }

    override fun enter(number: Number) {
        val b = if (number is BigDecimal) number else BigDecimal(number.toString())

        stack.push(type.cast(b))
    }


    override fun enter(number: String) {
        try {
            val b = BigDecimal(number)
            stack.push(type.cast(b))
        } catch (e: NumberFormatException) {
            logger.error("$number : Feil nummer format. ")
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
                for (i in 0 until noArgs) {
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
                val index = operator.ordinal - Op.X.ordinal
                if (index < variables.size) {
                    r.add(variables[index] as BigDecimal)
                } else {
                    throw IllegalStateException("has no value")
                }
            }
            else -> {
                logger.error("Implementation of ${operator.abbrev()} forgotten for ${type.simpleName}.")
            }
        }

        for (i in 0 until noArgs) {
            stack.poll()
        }

        for (res in r) {
            stack.push(type.cast(res))
        }
    }
}