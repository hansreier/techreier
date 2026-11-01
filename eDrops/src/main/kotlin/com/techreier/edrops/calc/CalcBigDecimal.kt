package com.techreier.edrops.calc

import com.techreier.edrops.exceptions.CalcException
import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext
import java.math.RoundingMode
import java.util.*


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

    override fun setAll(newVars: Map<Op, BigDecimal>) {
        vars.putAll(newVars)
    }

    override operator fun get(op: Op): BigDecimal {
        val variable = vars[op] ?: throw IllegalArgumentException("No variable ${op.abbrev()}")
        return variable
    }

    //TODO Too simple parsing
    override fun parseValue(input: String): BigDecimal {
        try {
            return input.toBigDecimal()
        } catch (_: NumberFormatException) {
            logger.error("$input : Error. Wrong BigDecimal format. ")
            throw CalcException(BIG_DECIMAL_FORMAT_ERROR, input)
        }
    }

    override fun enter(number: Number) {
        try {
            val b = when (number) {
                is BigDecimal -> number
                is BigInteger -> BigDecimal(number)
                else -> BigDecimal(number.toString())
            }
            stack.push(b)
        } catch (_: NumberFormatException) {
            throw CalcException(BIG_DECIMAL_FORMAT_ERROR, number.toString())
        } finally {
            traceStack(number.toString())
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

            Op.SQR -> {
                val x = arg.next() as BigDecimal
                r.add(x * x)
            }

            Op.SQRT -> {
                val x = arg.next() as BigDecimal
                r.add(x.sqrt(MathContext.DECIMAL128)) //TODO verify DECIMAL120
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

            Op.POW10 -> {
                val x = arg.next() as BigDecimal
                val exp = x.toInt() // Forventer heltallseksponent for 10^

                val result = if (exp >= 0) {
                    BigDecimal.ONE.movePointRight(exp)
                } else {
                    BigDecimal.ONE.movePointLeft(-exp)
                }

                r.add(result)
            }

            Op.LOG10 -> {
                val x = arg.next() as BigDecimal
                val logResult = Math.log10(x.toDouble())
                r.add(BigDecimal.valueOf(logResult))
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