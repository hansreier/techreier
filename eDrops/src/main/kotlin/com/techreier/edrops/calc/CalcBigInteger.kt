package com.techreier.edrops.calc

import com.techreier.edrops.config.logger
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode
import java.util.ArrayList

class CalcBigInteger<T> : Calc<T> {

    constructor(type: Class<T>, logOp: Boolean) : super(type) {
        if (type != BigInteger::class.java) {
            throw IllegalArgumentException(
                "Feil type ${type.simpleName} kalkulator, må være av type ${BigInteger::class.java.simpleName}."
            )
        }
        this.logOp = logOp
    }

    constructor(type: Class<T>) : this(type, false)

    override fun enter(number: Number) {
        val b: BigInteger? = when (number) {
            is BigDecimal -> number.setScale(0, RoundingMode.HALF_EVEN).toBigInteger()
            is Double -> BigDecimal.valueOf(number).setScale(0, RoundingMode.HALF_EVEN).toBigInteger()
            is BigInteger -> number
            is Long -> BigInteger.valueOf(number)
            is Int -> BigInteger.valueOf(number.toLong())
            else -> null
        }
        stack.push(type.cast(b))
        logOp("$number enter ")
        logStack()
    }

    override fun enter(number: Double) {
        val b = BigInteger.valueOf(Math.round(number))
        stack.push(type.cast(b))
        logOp("$b enter ")
        logStack()
    }

    override fun enter(number: Int) {
        val b = BigInteger.valueOf(number.toLong())
        stack.push(type.cast(b))
        logOp("$b enter ")
        logStack()
    }


    override fun enter(number: Long) {
        val b = BigInteger.valueOf(number)
        stack.push(type.cast(b))
        logOp("$b enter ")
        logStack()
    }

    override fun enter(number: String) {
        try {
            val b = BigInteger(number)
            stack.push(type.cast(b))
            logOp("$b enter ")
            logStack()
        } catch (e: NumberFormatException) {
            logger.error("$number : Feil nummer format. ")
            if (logOp) {
                logStack()
            }
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
                for (i in 0 until noArgs) {
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
                if (index < variables.size) {
                    r.add(variables[index] as BigInteger)
                } else {
                    throw IllegalStateException("has no value")
                }
            }
            else -> {
                logger.error("Implementation of ${operator.abbrev()} forgotten for ${type.simpleName}")
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