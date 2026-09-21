package com.techreier.edrops.calc

import com.techreier.edrops.config.logger
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.Test
import java.lang.Math
import kotlin.test.assertNotNull

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
            assertEquals(3 * x1 - (x1 * x1) / 4, result1, 1e-8) { "calculating: $input" }

            println("calculating with x2")
            calculator.variables[0] = x2
            expr.calculate()
            println("calculated with x2")
            val result2 = calculator.result()
            assertNotNull(result2)
            assertEquals(3 * x2 - (x2 * x2) / 4, result2, 1e-8) { "calculating: $input" }
        } else {
            fail("expression cannot be parsed")
        }
    }

    @Test
    fun advancedMultiVariableExpressionTest() {
        val input = "(x^3 * y - 2.5 * x * y^2 + 10) / (x^2 + y^2 + 1)"

        val x1 = 3.0
        val y1 = 2.0
        val x2 = 5.0
        val y2 = 1.5

        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val expr = Expr(calculator)

        expr.rotateTraceLevel()

        if (expr.parse(input)) {
            calculator.variables.add(x1)
            calculator.variables.add(y1)

            println("--- Kjører med x = $x1, y = $y1 ---")
            expr.calculate()
            val result1 = calculator.result()
            assertNotNull(result1)

            val expected1 = (Math.pow(x1, 3.0) * y1 - 2.5 * x1 * Math.pow(y1, 2.0) + 10) / (Math.pow(x1, 2.0) + Math.pow(y1, 2.0) + 1)
            assertEquals(expected1, result1, 1e-8) { "Feil for x=$x1, y=$y1" }

            calculator.variables[0] = x2
            calculator.variables[1] = y2

            println("--- Kjører med x = $x2, y = $y2 ---")
            expr.calculate()
            val result2 = calculator.result()
            assertNotNull(result2)

            val expected2 = (Math.pow(x2, 3.0) * y2 - 2.5 * x2 * Math.pow(y2, 2.0) + 10) / (Math.pow(x2, 2.0) + Math.pow(y2, 2.0) + 1)
            assertEquals(expected2, result2, 1e-8) { "Feil for x=$x2, y=$y2" }
        } else {
            fail("Klarte ikke å parse uttrykket: $input")
        }
    }

    @Test
    fun weirdParenthesisAndMultiVariableMonsterTest() {
        val input = "(( ( (x ^ 2) + (y ^ 2) ) / ( (x - y) ^ 2 + 1 ) ) * ( (z) - (1) ) ) + ( ( (2.5) * x ) / ( y ) )"

        val xVal = 3.0
        val yVal = 2.0
        val zVal = 5.0

        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val expr = Expr(calculator)

        expr.rotateTraceLevel()

        if (expr.parse(input)) {

            calculator.variables.add(xVal)
            calculator.variables.add(yVal)
            calculator.variables.add(zVal)

            expr.calculate()
            val result = calculator.result()
            assertNotNull(result)

            val term1 = (Math.pow(xVal, 2.0) + Math.pow(yVal, 2.0)) / (Math.pow(xVal - yVal, 2.0) + 1.0)
            val term2 = zVal - 1.0
            val term3 = (2.5 * xVal) / yVal
            val expected = (term1 * term2) + term3

            assertEquals(expected, result, 1e-8) { "Parseren eller stakken rotet det til i monsteret!" }
        } else {
            fail("Parseren kollapset totalt av det rare uttrykket: $input")
        }
    }

    @Test
    fun multiArgumentOperatorTest() {
        val input = "gyp(4 3)" // Math.sqrt(x * x - y * y)

        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)

        val expr = Expr(calculator)
        expr.traceLevel(Trace.ALL)
        val parsed = expr.parse(input)

        logger.info("Parseren godtok uttrykket: $parsed")

        if (parsed) {
            expr.calculate()
            val result = calculator.result()
            assertNotNull(result)
            assertEquals(Math.sqrt(7.0), result, 1E-9)
        }
    }

    @Test
    fun decimalCommaTest() {
        val input = "5E0 * 2,5"

        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val expr = Expr(calculator)

        val parsed = expr.parse(input)
        assertTrue(parsed)

        if (parsed) {
            expr.calculate()
            val result = calculator.result()
            assertNotNull(result)
            assertEquals(12.5, result, 1E-9)
        }
    }

    @Test
    fun ENotationTest() {
        val input = "5E0"

        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val expr = Expr(calculator)

        val parsed = expr.parse(input)
        assertTrue(parsed)

        expr.calculate()
        val result = calculator.result()
        logger.info("Resultat: $result")
    }

}