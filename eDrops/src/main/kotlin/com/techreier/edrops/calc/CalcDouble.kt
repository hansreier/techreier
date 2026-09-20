package com.techreier.edrops.calc

import java.util.ArrayList
import java.util.EnumSet

class CalcDouble<T>(
    type: Class<T>,
    printOp: Boolean = false
) : Calc<T>(type, printOp) {
    var degrees: Boolean = true

    init {
        if (type != Double::class.javaObjectType) {
            throw Exception("Feil parameter ${type.simpleName} kalkulator, må være ${Double::class.java.simpleName}.")
        }
        operators.addAll(
            EnumSet.of(
                Op.SIN, Op.COS, Op.TAN, Op.ASIN, Op.ACOS,
                Op.ATAN, Op.RAD, Op.DEG, Op.RADIANS, Op.DEGREES, Op.EX, Op.LN,
                Op.LOG10, Op.POW10, Op.E, Op.PI, Op.HYP, Op.GYP
            )
        )
    }

    override fun enter(number: Number) {
        stack.push(type.cast(number.toDouble()))
        logOp("$number enter ")
        logStack()
    }

    override fun enter(number: Double) {
        stack.push(type.cast(number))
        logOp("$number enter ")
        logStack()
    }

    override fun enter(number: Int) {
        val b = number.toDouble()
        stack.push(type.cast(b))
        logOp("$b enter ")
        logStack()
    }

    override fun enter(number: Long) {
        val b = number.toDouble()
        stack.push(type.cast(b))
        logOp("$b enter ")
        logStack()
    }

    override fun enter(number: String) {
        try {
            val b = number.toDouble()
            stack.push(type.cast(b))
            logOp("$b enter ")
            logStack()
        } catch (e: NumberFormatException) {
            println("$number : Feil nummer format. ")
            logStack()
        }
    }

    override fun op(operator: Op, number: String): T? {
        try {
            print("$number ")
            val b = number.toDouble()
            stack.push(type.cast(b))
            return op(operator)
        } catch (e: Exception) {
            println("Feil ${operator.abbrev()}: ${e.message}.")
            return null
        }
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
            Op.X2 -> {
                x = arg.next() as Double
                r.add(x * x)
            }
            Op.POW -> {
                y = arg.next() as Double
                x = arg.next() as Double
                r.add(Math.pow(x, y))
            }
            Op.SIN -> {
                x = arg.next() as Double
                if (degrees)
                    r.add(Math.sin(Math.toRadians(x)))
                else
                    r.add(Math.sin(x))
            }
            Op.ASIN -> {
                x = arg.next() as Double
                if (degrees)
                    r.add(Math.toDegrees(Math.asin(x)))
                else
                    r.add(Math.asin(x))
            }
            Op.COS -> {
                x = arg.next() as Double
                if (degrees)
                    r.add(Math.cos(Math.toRadians(x)))
                else
                    r.add(Math.cos(x))
            }
            Op.ACOS -> {
                x = arg.next() as Double
                if (degrees)
                    r.add(Math.toDegrees(Math.asin(x)))
                else
                    r.add(Math.asin(x))
            }
            Op.TAN -> {
                x = arg.next() as Double
                if (degrees)
                    r.add(Math.tan(Math.toRadians(x)))
                else
                    r.add(Math.tan(x))
            }
            Op.ATAN -> {
                x = arg.next() as Double
                if (degrees)
                    r.add(Math.toDegrees(Math.atan(x)))
                else
                    r.add(Math.atan(x))
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
                r.add(Math.exp(x))
            }
            Op.LN -> {
                x = arg.next() as Double
                r.add(Math.log(x))
            }
            Op.LOG10 -> {
                x = arg.next() as Double
                r.add(Math.log10(x))
            }
            Op.POW10 -> {
                x = arg.next() as Double
                r.add(Math.pow(10.0, x))
            }
            Op.HYP -> {
                y = arg.next() as Double
                x = arg.next() as Double
                r.add(Math.hypot(x, y))
            }
            Op.GYP -> {
                y = arg.next() as Double
                x = arg.next() as Double
                r.add(Math.sqrt(x * x - y * y))
            }
            Op.PI -> {
                r.add(Math.PI)
            }
            Op.E -> {
                r.add(Math.E)
            }
            Op.SUM -> {
                r.add(0.0)
                for (i in 0 until noArgs) {
                    val x = arg.next() as Double
                    r[0] = r.first() + x
                }
            }
            Op.REMOVE, Op.EMPTY -> {}
            Op.YX -> {
                r.add(arg.next() as Double)
                r.add(arg.next() as Double)
            }
            Op.X, Op.Y, Op.Z, Op.U, Op.V, Op.W -> {
                val index = operator.ordinal - Op.X.ordinal
                if (index < variables.size) {
                    r.add(variables[index] as Double)
                } else {
                    throw Exception("har ingen verdi")
                }
            }
            else -> {
                println("Implementering av ${operator.abbrev()} glemt for ${type.simpleName}")
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