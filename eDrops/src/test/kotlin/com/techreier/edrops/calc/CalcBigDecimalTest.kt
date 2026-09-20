package com.techreier.edrops.calc

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.random.Random

class CalcBigDecimalTest {

    @Test
    fun precisionAddTest() {
        println("TestPrecisionBigDecimal")
        val calculator = CalcBigDecimal(BigDecimal::class.javaObjectType)
        calculator.logStack = false

        calculator.enter(BigDecimal("0.1"))
        calculator.enter(BigDecimal("0.2"))
        calculator.op(Op.ADD) // Gir 0.3 på stakken

        // No float rounding mess
        assertEquals(
            BigDecimal("0.6"),
            calculator.op(Op.ADD, BigDecimal("0.3")),
            "0.1 + 0.2 + 0.3 skal være eksakt 0.6"
        )
        calculator.logStack()
    }

    @Test
    fun divisionScaleTest() {
        println("TestDivisionScaleBigDecimal")
        val calculator = CalcBigDecimal(BigDecimal::class.javaObjectType)
        calculator.logStack = false
        calculator.scale = 10

        // no float rounding mess
        calculator.enter(BigDecimal.ONE)
        calculator.enter(BigDecimal("3"))
        calculator.op(Op.DIVIDE)

        assertEquals(
            BigDecimal("0.3333333333"),
            calculator.result(),
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

        val calculator = CalcBigDecimal(BigDecimal::class.javaObjectType)
        calculator.logStack = false

        for (i in 0 until 10) {
            calculator.enter(numbers[i])
            if (i > 0) {
                expr.append("+")
                calculator.op(Op.ADD)
                sum = sum.add(numbers[i])
            } else {
                sum = numbers[0]
            }
            expr.append(numbers[i])
        }
        assertEquals(sum, calculator.result(), "Sum:$expr")
        calculator.logStack()
    }
}