package com.techreier.edrops.calc

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class ExprTest {

    @Test
    fun simpleExpressionTest() {
        println("TestExpression")
        val calculator: Calc<*> = CalcDouble(Double::class.javaObjectType, true)
        val input = "3*5- sin(90)"

        val expr = Expr(calculator)
        val parsed = expr.parse(input)
        assertTrue(parsed) { "parsing: $input" }

        expr.calculate()
        assertEquals(
            3 * 5 - Math.sin(Math.toRadians(90.0)),
            calculator.result() as Double,
            1e-10
        ) { "calculating: $input" }
    }

    @Test
    fun xExpressionTest() {
        val input = "3*x - (x^2 / 4)"
        val x1 = 4.0
        val x2 = 6.0

        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val expr = Expr(calculator)

        expr.rotateTraceLevel()
        expr.rotateTraceLevel()

        if (expr.parse(input)) {
            calculator.variables.add(x1)
            println("calculating with x1")
            expr.calculate()
            val result1 = calculator.result()
            assertNotNull(result1)
            assertEquals(3 * x1 - (x1 * x1) / 4, result1!!, 1e-8) { "calculating: $input" }

            println("calculating with x2")
            calculator.variables[0] = x2
            expr.calculate()
            println("calculated with x2")
            val result2 = calculator.result()
            assertNotNull(result2)
            assertEquals(3 * x2 - (x2 * x2) / 4, result2!!, 1e-8) { "calculating: $input" }
        } else {
            fail("expression cannot be parsed")
        }
    }
}