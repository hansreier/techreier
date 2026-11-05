package com.techreier.edrops.calc

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.random.Random

class CalcDoubleTest {

    @Test
    fun variousInputTest() {
        val calc = CalcDouble()
        calc.trace = Trace.ALL

        calc.enter("5")
        calc.enter(1)
        calc.operate(Op.ADD)

        calc.enter(2L)
        calc.operate(Op.ADD)

        calc.enter(BigInteger.valueOf(3))
        calc.operate(Op.ADD)

        calc.enter(0.4)
        calc.operate(Op.ADD)

        calc.enter(BigDecimal("0.6"))
        calc.operate(Op.ADD)

        assertEquals(
            12.0,
            calc.result(),
            "Mixed types"
        )
        calc.traceStack()
    }

    @Test
    fun addTest() {
        val calc = CalcDouble()
        calc.trace = Trace.ALL
        calc.enter(24.0)
        calc.enter(26.0)
        calc.enter(30.0)
        calc.operate(Op.ADD)
        calc.operate(Op.ADD)
        assertEquals(
            24.0 + 26.0 + 30.0,
            calc.result(),
            0.0001,
            "24 + 26 + 30"
        )
    }

    @Test
    fun sumTest() { //Enable later
        val calc = CalcDouble()
        calc.trace = Trace.ALL
     //   calc.enter(100)
        calc.enter(1.0)
        calc.enter(2.0)
        calc.operate(Op.SUBTRACT)
        calc.enter(24.0)
        calc.enter(26.0)
        calc.enter(30.0)
        calc.operate(Op.SUM)
        calc.enter(2)
        calc.operate(Op.MULTIPLY)
        assertEquals(
            ((1-2) + 24.0 + 26.0 + 30.0) * 2,
            calc.result(),
            0.0001,
            "24 + 26 + 30"
        )
    }

    @Test
    fun addMUltiplyPowerTest() {
        val calc = CalcDouble()
        calc.trace = Trace.ALL
        calc.enter(4.0)
        calc.enter(3.0)
        calc.enter(5.0)
        calc.enter(2.0)
        calc.operate(Op.POW)
        calc.operate(Op.MULTIPLY)
        calc.operate(Op.ADD)
        assertEquals(
            4.0 + 3 * 5*5,
            calc.result(),
            0.0001,
            "4 + 3 * 5^2"
        )
    }

    @Test
    fun AllResultsTest() {
        val calc = CalcDouble()
        calc.trace = Trace.ALL
        calc.enter(4.0)
        calc.enter(3.0)
        calc.operate(Op.POW)
        calc.enter(5.0)
        calc.enter(2.0)
        calc.operate(Op.MULTIPLY)
        val allResults = calc.allResults()
        assertThat(allResults).hasSize(2)
    }


    @Test
    fun randomAddTest() {
        val numbers = Array(10) {
            Random.nextDouble(0.0, 1000.0)
        }
        var sum = 0.0
        val expr = StringBuilder()

        val calc = CalcDouble()
        calc.trace = Trace.ALL

        for (i in 0 until 10) {
            numbers[i] = Random.nextDouble(0.0, 1000.0)
            calc.enter(numbers[i])
            if (i > 0) {
                expr.append("+")
                calc.operate(Op.ADD)
                sum += numbers[i]
            } else {
                sum = numbers[0]
            }
            expr.append(numbers[i])
        }
        assertEquals(sum, calc.result(), 0.0001, "Sum:$expr")
    }

}