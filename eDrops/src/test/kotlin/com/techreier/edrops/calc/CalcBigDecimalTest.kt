package com.techreier.edrops.calc

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.random.Random

class CalcBigDecimalTest {

    @Test
    fun precisionAddTest() {
        val calc = CalcBigDecimal()

        calc.enter(BigDecimal("0.1"))
        calc.enter(BigDecimal("0.2"))
        calc.op(Op.ADD)

        // No float rounding mess
        assertEquals(
            BigDecimal("0.3"),
            calc.result(),
            "0.1 + 0.2 shall be exact 0.3"
        )
        calc.traceStack()
    }

    @Test
    fun divisionScaleTest() {
        val calc = CalcBigDecimal()
        calc.scale = 10

        // no float rounding mess
        calc.enter(BigDecimal.ONE)
        calc.enter(BigDecimal("3"))
        calc.op(Op.DIVIDE)

        assertEquals(
            BigDecimal("0.3333333333"),
            calc.result(),
            "1 / 3 med skala 10"
        )
    }

    @Test
    fun randomAddTest() {
        val numbers = Array(10) {
            // Money scale
            BigDecimal.valueOf(Random.nextDouble(0.0, 1000.0)).setScale(2, RoundingMode.HALF_UP)
        }
        var sum = BigDecimal.ZERO
        val expr = StringBuilder()

        val calc = CalcBigDecimal()

        for (i in 0 until 10) {
            calc.enter(numbers[i])
            if (i > 0) {
                expr.append("+")
                calc.op(Op.ADD)
                sum = sum.add(numbers[i])
            } else {
                sum = numbers[0]
            }
            expr.append(numbers[i])
        }
        assertEquals(sum, calc.result(), "Sum:$expr")
        calc.traceStack()
    }
}