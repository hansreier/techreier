package com.techreier.edrops.calc

import com.techreier.edrops.calc.Op.Companion.basicOperators
import com.techreier.edrops.calc.Op.Companion.constants
import com.techreier.edrops.calc.Op.Companion.functions
import com.techreier.edrops.calc.Op.Companion.parenthesis
import com.techreier.edrops.calc.Op.Companion.variables
import com.techreier.edrops.exceptions.CalcException
import kotlin.Double.Companion.NaN
import kotlin.math.E
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.hypot
import kotlin.math.ln
import kotlin.math.log10
import kotlin.math.log2
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sign
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan
import kotlin.random.Random

private const val DEGREES_TO_RADIANS = PI / 180
private const val RADIANS_TO_DEGREES = 180 / PI

class CalcDouble : Calc<Double>() {
    override val type: Class<Double> = Double::class.javaObjectType
    var radians: Boolean = true

    override val vars: MutableMap<Op, Double> = mutableMapOf()

    init {
        operators += setOf(
            Op.SIN, Op.COS, Op.TAN, Op.ASIN, Op.ACOS,
            Op.ATAN, Op.RAD, Op.DEG, Op.EX, Op.LN, Op.LOG2, Op.SQRT,
            Op.E, Op.PI, Op.HYP, Op.GYP, Op.NEGATE, Op.RANDOM,
            Op.SELECT, Op.GT, Op.LT, Op.GTE, Op.LTE, Op.EQ, Op.NEQ, Op.NOT, Op.AND, Op.OR
        )

        //Check for consistency in set of enums
        val missing = operators.filter { it !in (functions + constants +basicOperators + variables + parenthesis) }
        require(missing.isEmpty()) {
            "Operators missing in operator lists: $missing"
        }

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

            Op.GT -> {
                y = arg.next() as Double
                x = arg.next() as Double
                if (isNotNan(x,y))
                    r.add(if (x - y > tol(x, y)) 1.0 else 0.0)
                else
                    r.add(NaN)
            }

            Op.LT -> {
                y = arg.next() as Double
                x = arg.next() as Double
                if (isNotNan(x,y))
                    r.add(if (y - x > tol(x, y)) 1.0 else 0.0)
                else
                    r.add(NaN)
            }

            Op.GTE -> {
                y = arg.next() as Double
                x = arg.next() as Double
                if (isNotNan(x,y))
                    r.add(if (x - y >= -tol(x, y)) 1.0 else 0.0)
                else r.add(NaN)
            }

            Op.LTE -> {
                y = arg.next() as Double
                x = arg.next() as Double
                if (isNotNan(x,y))
                    r.add(if (y - x >= -tol(x, y)) 1.0 else 0.0)
                else r.add(NaN)
            }

            Op.EQ -> {
                y = arg.next() as Double
                x = arg.next() as Double
                if (isNotNan(x,y))
                    r.add(if (abs(x - y) <= tol(x, y)) 1.0 else 0.0)
                else r.add(NaN)
            }

            Op.NEQ -> {
                y = arg.next() as Double
                x = arg.next() as Double
                if (isNotNan(x,y))
                    r.add(if (abs(x - y) > tol(x, y)) 1.0 else 0.0)
                else r.add(NaN)
            }


            Op.AND -> {
                y = arg.next() as Double
                x = arg.next() as Double
                if (isValidBooleans(x,y))
                    r.add(if ((x > 0.5) && (y > 0.5))  1.0 else 0.0)
                else
                    r.add(NaN)
            }

            Op.OR -> {
                y = arg.next() as Double
                x = arg.next() as Double
                if (isValidBooleans(x,y))
                    r.add(if ((x > 0.5) || (y > 0.5))  1.0 else 0.0)
                else
                    r.add(NaN)
            }

            Op.NEGATE -> {
                x = arg.next() as Double
                r.add(-x)
            }

            Op.ABS -> {
                x = arg.next() as Double
                r.add(abs(x))
            }

            Op.SIGN -> {
                x = arg.next() as Double
                r.add(sign(x))
            }

            Op.SQR -> {
                x = arg.next() as Double
                r.add(x * x)
            }

            Op.SQRT -> {
                x = arg.next() as Double
                r.add(sqrt(x))
            }

            Op.POW -> {
                y = arg.next() as Double
                x = arg.next() as Double
                r.add(x.pow(y))
            }

            Op.SIN -> {
                x = arg.next() as Double
                if (radians)
                    r.add(sin(x).cleanZero())
                else
                    r.add(sin(x * DEGREES_TO_RADIANS).cleanZero())
            }

            Op.ASIN -> {
                x = arg.next() as Double
                if (radians)
                    r.add(asin(x))
                else
                    r.add(asin(x) * RADIANS_TO_DEGREES)
            }

            Op.COS -> {
                x = arg.next() as Double
                if (radians)
                    r.add(cos(x).cleanZero())
                else
                    r.add(cos(x * DEGREES_TO_RADIANS).cleanZero())
            }

            Op.ACOS -> {
                x = arg.next() as Double
                if (radians)
                    r.add(asin(x))
                else
                    r.add(acos(x) * RADIANS_TO_DEGREES)
            }

            Op.TAN -> {
                x = arg.next() as Double
                if (radians)
                    r.add(tan(x).cleanZero())
                else
                    r.add(tan(x * DEGREES_TO_RADIANS).cleanZero())
            }

            Op.ATAN -> {
                x = arg.next() as Double
                if (radians)
                    r.add(atan(x))
                else
                r.add(atan(x) * RADIANS_TO_DEGREES)
            }

            Op.RAD -> {
                x = arg.next() as Double
            r.add(x * DEGREES_TO_RADIANS)
            }

            Op.DEG -> {
                x = arg.next() as Double
            r.add(x * RADIANS_TO_DEGREES)
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

            Op.LOG2 -> {
                x = arg.next() as Double
                r.add(log2(x))
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

            Op.TRUE -> {
                r.add(1.0)
            }

            Op.FALSE -> {
                r.add(0.0)
            }

            Op.NOT -> {
                x = arg.next() as Double
                if (isValidBooleans(x))
                    r.add( if (x < 0.5) 1.0 else 0.0)
                else
                    r.add(NaN)
            }

            Op.SELECT -> { // Mimics an IF statement
                val y = arg.next() as Double
                val x = arg.next() as Double
                val flag = arg.next() as Double

                if (isValidBooleans(flag)) {
                    r.add(if (abs(flag) < EPSILON) y else x)
                } else {
                    r.add(NaN)
                }
            }

            Op.E -> {
                r.add(E)
            }
            Op.RANDOM -> {
                r.add(Random.nextDouble())
            }

            Op.SUM -> {
                r.add(0.0)
                repeat(noArgs) {
                    val x = arg.next() as Double
                    r[0] = r.first() + x
                }
            }

            Op.MAX -> {
                var maximum = arg.next() as Double
                repeat(noArgs - 1) {
                    maximum = max(maximum, arg.next() as Double)
                }
                r.add(maximum)
            }

            Op.MIN -> {
                var minimum = arg.next() as Double
                repeat(noArgs - 1) {
                    minimum = min(minimum, arg.next() as Double)
                }
                r.add(minimum)
            }

            Op.REMOVE, Op.EMPTY -> {}
            Op.YX -> {
                r.add(arg.next() as Double)
                r.add(arg.next() as Double)
            }

            in variables -> {
                val variable = vars[operator]
                variable ?: throw (CalcException(UNKNOWN_VARIABLE, operator.abbrev()))
                r.add(variable)
            }

            else -> {
                val errorText = "Implementation of ${operator.abbrev()} forgotten for ${type.simpleName}"
                logger.error("Implementation of ${operator.abbrev()} forgotten for ${type.simpleName}")
                throw (CalcException(UNKNOWN_OPERATOR, operator.abbrev(), errorText))
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
        if (abs(this) < EPSILON) 0.0 else this

    fun isValidBooleans(vararg values: Double): Boolean {
        return values.all { (abs(it) < EPSILON) || (abs(it - 1.0) < EPSILON) }
    }

    fun isNotNan(vararg values: Double): Boolean {
        return values.all { !it.isNaN() }
    }

    // Qualy Double equals
    private fun tol(x: Double, y: Double) = EPSILON * maxOf(1.0, abs(x), abs(y))

}

