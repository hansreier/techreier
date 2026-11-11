package com.techreier.edrops.calc

import com.techreier.edrops.calc.utils.TraceLevel
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import kotlin.test.assertEquals

class ExprArithmeticTest {

    lateinit var calc: CalcDouble

    @BeforeEach
    fun setUp() {
        calc = CalcDouble()
    }

    @Test
    fun happy42Test() {
        val input = "42"
        val expr = Expr(calc, input)
        calc.tracelevel = TraceLevel.ALL
        expr.parse()
        expr.calculate()
        val singleResult = calc.result()
        assertNotNull(singleResult)
        assertEquals(42.0, singleResult, 1e-9)
    }

    @Test
    fun operatorsDifferentLevelTest() {
        val input = "3 + 5 * 4 + 1"
        val expr = Expr(calc, input)
        calc.tracelevel = TraceLevel.ALL
        expr.parse()
        expr.calculate()
        assertEquals(24.0,calc.result(), 1e-9)
    }

    @Test
    fun repeatedMultiplyTest() {
        val input = "5 * 3 * 4"
        val expr = Expr(calc, input)
        calc.tracelevel = TraceLevel.ALL
        expr.parse()
        expr.calculate()
        val result = calc.result()
        assertEquals(60.0, result, 1e-8, "calculation: " + input)
    }

    @Test
    fun withParanthesisTest() {
        calc.tracelevel = TraceLevel.ALL
        val input = "(3 + 5 * (2 + 3) )/ 2"
        val expr = Expr(calc, input)
        expr.parse()
        expr.calculate()
        val result = calc.result()

        assertEquals(14.0, result, 1e-8)
    }

    @Test
    fun withParanthesisSimpleTest() {
        calc.tracelevel = TraceLevel.ALL
        val input = "(3 + 5) * 2"
        val expr = Expr(calc, input)
        expr.parse()
        expr.calculate()
        val result = calc.result()

        assertEquals(16.0, result, 1e-8)
    }

    @Test
    fun closeMinusTest() {
        val input = "2^-2(4)"
        val expr = Expr(calc, input)
        expr.trace(TraceLevel.ALL)
        expr.parse()
        expr.calculate()
        assertEquals(1.0, expr.result(), 1E-9 )
    }

    @Test
    fun minusMinusTest() {
        val input = "3- -3"
        val expr = Expr(calc, input)
        expr.trace(TraceLevel.ALL)
        expr.parse()
        expr.calculate()
        assertEquals(6.0, expr.result() )
    }

    @Test
    fun negativeNumberPowerTest() {
        val input = "-2^2"

        val expr = Expr(calc, input)
        expr.trace(TraceLevel.ALL)
        expr.parse()
        expr.calculate()
        assertEquals(4.0, expr.result(), 1e-9 )
    }

    @Test
    fun negativeNumberPower2Test() {
        val input = "3 - -2^2"

        val expr = Expr(calc, input)
        expr.trace(TraceLevel.ALL)
        expr.parse()
        expr.calculate()
        assertEquals(-1.0, expr.result(), 1e-9 )
    }

    @Test
    fun operatorNegateTest() {
        val expr = Expr(calc, " 3 * - 3")
        expr.trace(TraceLevel.ALL)
        expr.parse()
        expr.calculate()
        assertEquals(-9.0 , calc.result())
    }

    @Test
    fun negateParenthesisTest() {
        val expr = Expr(calc, "-(3+5)")
        expr.trace(TraceLevel.ALL)
        expr.parse()
        expr.calculate()
        assertEquals(-8.0, calc.result())
    }

    @Test
    fun overFlowTest() {
        val expr = Expr(calc, "9^9^9")
        expr.trace(TraceLevel.ALL)
        expr.parse()
        expr.calculate()
        assertEquals(Double.POSITIVE_INFINITY, expr.result())
    }

    @Test
    fun parenthesisHellTest() {
        val input = "((((((((((((((((((((((((((((((((((((((((((((((((((1))))))))))))))))))))))))))))))))))))))))))))))))))"
        val expr = Expr(calc, input)
        expr.trace(TraceLevel.ALL)
        expr.parse()
        expr.calculate()
        assertEquals(1.0, expr.result())
    }

}
