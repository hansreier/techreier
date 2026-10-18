package com.techreier.edrops.calc

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.random.Random

class CalcDoubleTest {

    @Test
    fun addTest() {
        val calc = CalcDouble()
        calc.trace = Trace.ALL
        calc.enter(24.0)
        calc.enter(26.0)
        calc.enter(30.0)
        calc.op(Op.ADD)
        calc.op(Op.ADD)
        calc.op(Op.ADD)
        assertEquals(
            24.0 + 26.0 + 30.0,
            calc.result(),
            0.0001,
            "24 + 26 + 30"
        )
        calc.traceStack()
    }

    @Test
    fun addMUltiplyPowerTest() {
        val calc = CalcDouble()
        calc.trace = Trace.ALL
        calc.enter(4.0)
        calc.enter(3.0)
        calc.enter(5.0)
        calc.enter(2.0)
        calc.op(Op.POW)
        calc.op(Op.MULTIPLY)
        calc.op(Op.ADD)
    }

    @Test
    fun randomAddTest() {
        val numbers = Array(10) {
            Random.nextDouble(0.0, 1000.0)
        }
        var sum = 0.0
        val expr = StringBuilder()

        val calc = CalcDouble()

        for (i in 0 until 10) {
            numbers[i] = Random.nextDouble(0.0, 1000.0)
            calc.enter(numbers[i])
            if (i > 0) {
                expr.append("+")
                calc.op(Op.ADD)
                sum += numbers[i]
            } else {
                sum = numbers[0]
            }
            expr.append(numbers[i])
        }
        assertEquals(sum, calc.result(), 0.0001, "Sum:$expr")
        calc.traceStack()
    }
}