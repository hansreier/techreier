package com.techreier.edrops.calc

import com.techreier.edrops.calc.Op.Companion.basicOperators
import com.techreier.edrops.calc.Op.Companion.constants
import com.techreier.edrops.calc.Op.Companion.functions
import com.techreier.edrops.calc.Op.Companion.parenthesis
import com.techreier.edrops.calc.Op.Companion.variables
import com.techreier.edrops.exceptions.CalcException
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode
import java.util.ArrayList
import kotlin.collections.set


class CalcBigInteger : Calc<BigInteger>() {
    override val type: Class<BigInteger> = BigInteger::class.java

    override val vars: MutableMap<Op, BigInteger> = mutableMapOf()


    init {
        if (type != BigInteger::class.java) {
            throw IllegalArgumentException("Feil parameter ${type.simpleName} , må være ${BigInteger::class.java.simpleName}.")
        }
        val missing = operators.filter { it !in (functions + constants +basicOperators + variables + parenthesis) }
        require(missing.isEmpty()) {
            "Operators missing in operator lists: $missing"
        }
    }

    override operator fun set(op: Op, value: BigInteger) {
        vars[op] = value
    }

    override fun setAll(newVars: Map<Op, BigInteger>) {
        vars.putAll(newVars)
    }

    override operator fun get(op: Op): BigInteger {
        val variable = vars[op] ?: throw IllegalArgumentException("No variable ${op.abbrev()}")
        return variable
    }

    // TODO Too simple parsing
    override fun parseValue(input: String): BigInteger {
        try {
            return input.toBigInteger()
        } catch (_: NumberFormatException) {
            logger.error("$input : Error. Wrong BigInteger format. ")
            throw CalcException(BIG_INTEGER_FORMAT_ERROR, input)
        }
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
            stack.addFirst(b)
        } catch (_: NumberFormatException) {
            throw CalcException(BIG_INTEGER_FORMAT_ERROR, number.toString())
        } finally {
            traceStack(number.toString())
        }
    }

    override fun opr(operator: Op, noArgs: Int) {
        val arg = stack.iterator()
        val r = ArrayList<BigInteger>()

        val x: BigInteger
        val y: BigInteger

        when (operator) {
            Op.ADD -> {
                y = arg.next()
                x = arg.next()
                r.add(x.add(y))
            }
            Op.SUBTRACT -> {
                y = arg.next()
                x = arg.next()
                r.add(x.subtract(y))
            }
            Op.MULTIPLY -> {
                y = arg.next()
                x = arg.next()
                r.add(x.multiply(y))
            }
            Op.DIVIDE -> {
                y = arg.next()
                x = arg.next()
                r.add(x.divide(y))
            }
            Op.NEGATE -> {
                x = arg.next()
                r.add(-x)
            }

            Op.ABS -> {
                x = arg.next()
                r.add(x.abs())
            }

            Op.TRUE -> {
                r.add(BigInteger.ONE)
            }

            Op.FALSE -> {
                r.add(BigInteger.ZERO)
            }

            Op.SIGN -> {
                val x = arg.next()
                r.add(BigInteger.valueOf(x.signum().toLong()))
            }

            Op.SQR -> {
                x = arg.next()
                r.add(x.multiply(x))
            }

            Op.POW -> {
                y = arg.next()
                x = arg.next()
                r.add(x.pow(y.intValueExact()))
            }

            Op.POW10 -> {
                val x = arg.next()
                r.add(BigInteger.TEN.pow(x.intValueExact()))
            }

            Op.LOG10 -> {
                val x = arg.next()
                val logVal = if (x > BigInteger.ZERO) {
                    BigInteger.valueOf((x.toString().length - 1).toLong())
                } else {
                    BigInteger.ZERO
                }
                r.add(logVal)
            }

            Op.SUM -> {
                r.add(BigInteger.ZERO)
               repeat (noArgs) {
                    val x = arg.next()
                    r[0] = r[0].add(x)
                }
            }

            Op.MAX -> {
                var maximum = arg.next()
                repeat(noArgs - 1) {
                    maximum = maximum.max(arg.next() )
                }
                r.add(maximum)
            }

            Op.MIN -> {
                var minimum = arg.next()
                repeat(noArgs - 1) {
                    minimum = minimum.min(arg.next() )
                }
                r.add(minimum)
            }

            Op.REMOVE, Op.EMPTY -> {}
            Op.YX -> {
                r.add(arg.next())
                r.add(arg.next())
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
            stack.removeFirst()
        }

        for (res in r) {
            stack.addFirst(type.cast(res))
        }
    }
}