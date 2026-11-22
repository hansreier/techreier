package com.techreier.edrops.calc

import com.techreier.edrops.calc.Op.Companion.basicOperators
import com.techreier.edrops.calc.Op.Companion.constants
import com.techreier.edrops.calc.Op.Companion.functions
import com.techreier.edrops.calc.Op.Companion.parenthesis
import com.techreier.edrops.calc.Op.Companion.variables
import com.techreier.edrops.calc.utils.VarType
import com.techreier.edrops.calc.utils.varType
import com.techreier.edrops.exceptions.CalcException
import com.techreier.edrops.util.Trace.err
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
import kotlin.math.roundToInt
import kotlin.math.sign
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan
import kotlin.random.Random
import kotlin.reflect.KClass

private const val DEGREES_TO_RADIANS = PI / 180
private const val RADIANS_TO_DEGREES = 180 / PI

class CalcDouble : Calc<Double>() {
    override val type: KClass<Double> = Double::class
 //   var radians: Boolean = true

    override val vars: MutableMap<OpI, Double> = mutableMapOf()

    init {
        operators += setOf(
            Op.SIN, Op.COS, Op.TAN, Op.ASIN, Op.ACOS,
            Op.ATAN, Op.RAD, Op.DEG, Op.EX, Op.LN, Op.LOG2, Op.SQRT,
            Op.E, Op.PI, Op.HYP, Op.GYP, Op.NEGATE, Op.RANDOM,
            Op.SELECT, Op.GT, Op.LT, Op.GTE, Op.LTE, Op.EQ, Op.NEQ, Op.NOT, Op.AND, Op.OR
        )

        this[Op.RADIANS] = TRUE
        //Check for consistency in set of enums
        val missing = operators.filter { it !in (functions + constants +basicOperators + variables + parenthesis) }
        require(missing.isEmpty()) {
            "Operators missing in operator lists: $missing"
        }

    }

    override operator fun set(op: OpI, value: Double) {
        vars[op] = when (op.varType) {
            VarType.NUMBER -> value
            VarType.BOOLEAN -> if (value > 0.5) 1.0 else 0.0
            VarType.INTEGER -> if (value.isFinite()) value.roundToInt().toDouble() else NaN
        }
    }

    override fun set(op: OpI) {
        operators += op
        vars[op] = 0.0
        customVariables += op
    }

    override fun setAll(newVars: Map<OpI, Double>) {
        vars.putAll(newVars)
    }

    override operator fun get(op: OpI): Double {
        val variable = vars[op] ?: throw IllegalArgumentException("No variable ${op.abbrev}")
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
        stack.addFirst(number.toDouble())
        traceStack(number.toString())
    }

    override fun opr(operator: OpI, noArgs: Int) {
        val x: Double
        val y: Double
        val arg = stack.iterator()
        val r = ArrayList<Double>()

        val radians = this[Op.RADIANS] > 0.5

        when (operator) {
            Op.ADD -> {
                y = arg.next()
                x = arg.next()
                r.add(x + y)
            }

            Op.SUBTRACT -> {
                y = arg.next()
                x = arg.next()
                r.add(x - y)
            }

            Op.MULTIPLY -> {
                y = arg.next()
                x = arg.next()
                r.add(x * y)
            }

            Op.DIVIDE -> {
                y = arg.next()
                x = arg.next()
                r.add(x / y)
            }

            Op.GT -> {
                y = arg.next()
                x = arg.next()
                r.add(if (x - y > tol(x, y)) 1.0 else 0.0)
            }

            Op.LT -> {
                y = arg.next()
                x = arg.next()
                r.add(if (y - x > tol(x, y)) 1.0 else 0.0)
            }

            Op.GTE -> {
                y = arg.next()
                x = arg.next()
                r.add(if (x - y >= -tol(x, y)) 1.0 else 0.0)
            }

            Op.LTE -> {
                y = arg.next()
                x = arg.next()
                r.add(if (y - x >= -tol(x, y)) 1.0 else 0.0)
            }

            Op.EQ -> {
                y = arg.next()
                x = arg.next()
                r.add(if (abs(x - y) <= tol(x, y)) 1.0 else 0.0)
            }

            Op.NEQ -> {
                y = arg.next()
                x = arg.next()
                r.add(if (!(abs(x - y) <= tol(x, y))) 1.0 else 0.0)
            }


            Op.AND -> {
                y = arg.next()
                x = arg.next()
                r.add(if ((x > 0.5) && (y > 0.5))  1.0 else 0.0)
            }

            Op.OR -> {
                y = arg.next()
                x = arg.next()
                r.add(if ((x > 0.5) || (y > 0.5))  1.0 else 0.0)
            }

            Op.NEGATE -> {
                x = arg.next()
                r.add(-x)
            }

            Op.ABS -> {
                x = arg.next()
                r.add(abs(x))
            }

            Op.SIGN -> {
                x = arg.next()
                r.add(sign(x))
            }

            Op.SQR -> {
                x = arg.next()
                r.add(x * x)
            }

            Op.SQRT -> {
                x = arg.next()
                r.add(sqrt(x))
            }

            Op.POW -> {
                y = arg.next()
                x = arg.next()
                r.add(x.pow(y))
            }

            Op.SIN -> {
                x = arg.next()
                if (this[Op.RADIANS] >0.5)
                    r.add(sin(x).cleanZero())
                else
                    r.add(sin(x * DEGREES_TO_RADIANS).cleanZero())
            }

            Op.ASIN -> {
                x = arg.next()
                if (radians)
                    r.add(asin(x))
                else
                    r.add(asin(x) * RADIANS_TO_DEGREES)
            }

            Op.COS -> {
                x = arg.next()
                if (radians)
                    r.add(cos(x).cleanZero())
                else
                    r.add(cos(x * DEGREES_TO_RADIANS).cleanZero())
            }

            Op.ACOS -> {
                x = arg.next()
                if (radians)
                    r.add(asin(x))
                else
                    r.add(acos(x) * RADIANS_TO_DEGREES)
            }

            Op.TAN -> {
                x = arg.next()
                if (radians)
                    r.add(tan(x).cleanZero())
                else
                    r.add(tan(x * DEGREES_TO_RADIANS).cleanZero())
            }

            Op.ATAN -> {
                x = arg.next()
                if (radians)
                    r.add(atan(x))
                else
                r.add(atan(x) * RADIANS_TO_DEGREES)
            }

            Op.RAD -> {
                x = arg.next()
            r.add(x * DEGREES_TO_RADIANS)
            }

            Op.DEG -> {
                x = arg.next()
            r.add(x * RADIANS_TO_DEGREES)
            }

            Op.EX -> {
                x = arg.next()
                r.add(exp(x))
            }

            Op.LN -> {
                x = arg.next()
                r.add(ln(x))
            }

            Op.LOG10 -> {
                x = arg.next()
                r.add(log10(x))
            }

            Op.LOG2 -> {
                x = arg.next()
                r.add(log2(x))
            }

            Op.POW10 -> {
                x = arg.next()
                r.add(10.0.pow(x))
            }

            Op.HYP -> {
                y = arg.next()
                x = arg.next()
                r.add(hypot(x, y))
            }

            Op.GYP -> {
                y = arg.next()
                x = arg.next()
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
                x = arg.next()
                r.add(if (x > 0.5) 0.0 else 1.0)
            }

            Op.SELECT -> { // Mimics an IF statement
                val y = arg.next()
                val x = arg.next()
                val flag = arg.next()
                r.add(if (flag > 0.5) x else y)
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
                    val x = arg.next()
                    r[0] = r.first() + x
                }
            }

            Op.MAX -> {
                var maximum = arg.next()
                repeat(noArgs - 1) {
                    maximum = max(maximum, arg.next())
                }
                r.add(maximum)
            }

            Op.MIN -> {
                var minimum = arg.next()
                repeat(noArgs - 1) {
                    minimum = min(minimum, arg.next())
                }
                r.add(minimum)
            }

            Op.REMOVE -> {}
            Op.YX -> {
                r.add(arg.next())
                r.add(arg.next())
            }

            in (variables + customVariables) -> {
                val variable = vars[operator]
                variable ?: throw (CalcException(UNKNOWN_VARIABLE, operator.abbrev))
                r.add(variable)
            }

            else -> {
                val errorText = "Implementation of ${operator.abbrev} forgotten for ${type.simpleName}"
                err("Implementation of ${operator.abbrev} forgotten for ${type.simpleName}")
                throw (CalcException(UNKNOWN_OPERATOR, operator.abbrev, errorText))
            }
        }

        repeat(noArgs) {
            stack.removeFirst()
        }

        for (res in r) {
            stack.addFirst(res)
        }
    }

    //to be used for rounding to zero for some selected functions
    private fun Double.cleanZero(): Double =
        if (abs(this) < EPSILON) 0.0 else this

    // Qualy Double equals
    private fun tol(x: Double, y: Double) = EPSILON * maxOf(1.0, abs(x), abs(y))

}

