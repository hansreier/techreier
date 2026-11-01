package com.techreier.edrops.calc

import com.techreier.edrops.calc.Op.Companion.variables
import com.techreier.edrops.exceptions.CalcException
import java.lang.Math.random
import java.util.*
import kotlin.math.E
import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.hypot
import kotlin.math.ln
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

class CalcDouble : Calc<Double>() {
    override val type: Class<Double> = Double::class.javaObjectType
    var degrees: Boolean = true
    var epsilon = 1e-12

    override val vars: MutableMap<Op, Double> = EnumMap(Op::class.java)

    init {
        operators += setOf(
            Op.SIN, Op.COS, Op.TAN, Op.ASIN, Op.ACOS,
            Op.ATAN, Op.RAD, Op.DEG, Op.RADIANS, Op.DEGREES, Op.EX, Op.LN,
            Op.LOG10, Op.POW10, Op.E, Op.PI, Op.HYP, Op.GYP, Op.NEGATE, Op.RANDOM
        )
    }

    override operator fun set(op: Op, value: Double) {
        vars[op] = value
    }

    override fun setAll(newVars: Map<Op, Double>) {
        vars.putAll(newVars)
    }

    override operator fun get(op: Op): Double {
        val variable = vars[op] ?: throw IllegalArgumentException("No variable ${op.abbrev()}")
        return variable
    }

    override fun parseValue(input: String): Double {
        try {
            return input.toDouble()
        } catch (_: NumberFormatException) {
            throw CalcException(DOUBLE_FORMAT_ERROR, input)
        }
    }

    override fun enter(number: Number) {
        stack.push(number.toDouble())
        traceStack(number.toString())
    }

    override fun opr(operator: Op, noArgs: Int) {
        val x: Double
        val y: Double
        val arg = stack.iterator()
        val r = ArrayList<Double>()

        when (operator) {
            Op.ADD -> {
                y = arg.next() as Double
                x = arg.next() as Double
                r.add(x + y)
            }

            Op.SUBTRACT -> {
                y = arg.next() as Double
                x = arg.next() as Double
                r.add(x - y)
            }

            Op.MULTIPLY -> {
                y = arg.next() as Double
                x = arg.next() as Double
                r.add(x * y)
            }

            Op.DIVIDE -> {
                y = arg.next() as Double
                x = arg.next() as Double
                r.add(x / y)
            }

            Op.NEGATE -> {
                x = arg.next() as Double
                r.add(-x)
            }

            Op.X2 -> {
                x = arg.next() as Double
                r.add(x * x)
            }

            Op.POW -> {
                y = arg.next() as Double
                x = arg.next() as Double
                r.add(x.pow(y))
            }

            Op.SIN -> {
                x = arg.next() as Double
                if (degrees)
                    r.add(sin(Math.toRadians(x)).cleanZero())
                else
                    r.add(sin(x).cleanZero())
            }

            Op.ASIN -> {
                x = arg.next() as Double
                if (degrees)
                    r.add(Math.toDegrees(asin(x)))
                else
                    r.add(asin(x))
            }

            Op.COS -> {
                x = arg.next() as Double
                if (degrees)
                    r.add(cos(Math.toRadians(x)).cleanZero())
                else
                    r.add(cos(x).cleanZero())
            }

            Op.ACOS -> {
                x = arg.next() as Double
                if (degrees)
                    r.add(Math.toDegrees(acos(x)))
                else
                    r.add(asin(x))
            }

            Op.TAN -> {
                x = arg.next() as Double
                if (degrees)
                    r.add(tan(Math.toRadians(x)).cleanZero())
                else
                    r.add(tan(x).cleanZero())
            }

            Op.ATAN -> {
                x = arg.next() as Double
                if (degrees)
                    r.add(Math.toDegrees(Math.atan(x)))
                else
                    r.add(atan(x))
            }

            Op.RAD -> {
                x = arg.next() as Double
                r.add(Math.toRadians(x))
            }

            Op.DEG -> {
                x = arg.next() as Double
                r.add(Math.toDegrees(x))
            }

            Op.RADIANS -> {
                degrees = false
            }

            Op.DEGREES -> {
                degrees = true
            }

            Op.EX -> {
                x = arg.next() as Double
                r.add(exp(x))
            }

            Op.LN -> {
                x = arg.next() as Double
                r.add(ln(x))
            }

            Op.LOG10 -> {
                x = arg.next() as Double
                r.add(log10(x))
            }

            Op.POW10 -> {
                x = arg.next() as Double
                r.add(10.0.pow(x))
            }

            Op.HYP -> {
                y = arg.next() as Double
                x = arg.next() as Double
                r.add(hypot(x, y))
            }

            Op.GYP -> {
                y = arg.next() as Double
                x = arg.next() as Double
                r.add(sqrt(x * x - y * y))
            }

            Op.PI -> {
                r.add(PI)
            }

            Op.E -> {
                r.add(E)
            }
            Op.RANDOM -> {
                r.add(random())
            }

            Op.SUM -> {
                r.add(0.0)
                repeat(noArgs) {
                    val x = arg.next() as Double
                    r[0] = r.first() + x
                }
            }

            Op.REMOVE, Op.EMPTY -> {}
            Op.YX -> {
                r.add(arg.next() as Double)
                r.add(arg.next() as Double)
            }

            Op.SEPARATOR -> { //Separator really does nothing but to be removed
                val x = arg.next() as Double
                r.add(arg.next() as Double)
                r.add(x)
            }

            in variables -> {
                val variable = vars[operator]
                variable ?: throw (CalcException(UNKNOWN_VARIABLE, operator.abbrev()))
                r.add(variable)
            }

            else -> {
                logger.error("Implementation of ${operator.abbrev()} forgotten for ${type.simpleName}")
            }
        }

        repeat(noArgs) {
            stack.poll()
        }

        for (res in r) {
            stack.push(res)
        }
    }

    //to be used for rounding to zero for some selected functions
    private fun Double.cleanZero(): Double =
        if (kotlin.math.abs(this) < epsilon) 0.0 else this
}

