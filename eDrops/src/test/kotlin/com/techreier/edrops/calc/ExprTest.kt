package com.techreier.edrops.calc

import com.techreier.edrops.config.logger
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import kotlin.math.sqrt
import kotlin.test.assertNotNull

class ExprTest {

    @Test
    fun missingMultiplcatorTest() {
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val x = 4.0
        val input = "3(x)"

        val expr = Expr(calculator, input)
        val parsed = expr.parse()
        assertTrue(parsed) { "parsing: $input" }

        calculator.variables.add(x)
        expr.calculate()
        assertEquals(4.0, calculator.result() as Double, 1e-10)
        // TODO Add check on rest, and option for error if it is a rest.
    }

    @Test
    fun basicOperatorTest() {
        val calculator: Calc<*> = CalcDouble(Double::class.javaObjectType, true)
        val input = "3 + pi"

        val expr = Expr(calculator, input)
        val parsed = expr.parse()
        assertTrue(parsed) { "parsing: $input" }

        expr.calculate()
        assertEquals(3 + Math.PI, calculator.result() as Double, 1e-10)
    }

    @Test
    fun simpleExpressionTest() {
        val calculator: Calc<*> = CalcDouble(Double::class.javaObjectType, true)
        val input = "3 * 5 - sin(90)"

        val expr = Expr(calculator, input)
        val parsed = expr.parse()
        assertTrue(parsed) { "parsing: $input" }

        expr.calculate()
        assertEquals(
            3 * 5 - Math.sin(Math.toRadians(90.0)),
            calculator.result() as Double,
            1e-10
        ) { "calculating: $input" }
    }

    @Test
    fun calculateSeriesTest() {
        val input = "3*x - (x^2 / 4)"
        val x1 = 4.0
        val x2 = 6.0

        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val expr = Expr(calculator, input)

        expr.rotateTraceLevel()
        expr.rotateTraceLevel()

        if (expr.parse()) {
            calculator.variables.add(x1)
            logger.info("calculating with x1")
            expr.calculate()
            val result1 = calculator.result()
            assertNotNull(result1)
            assertEquals(3 * x1 - (x1 * x1) / 4, result1, 1e-8) { "calculating: $input" }

            logger.info("calculating with x2")
            calculator.variables[0] = x2
            expr.calculate()
            val result2 = calculator.result()
            assertNotNull(result2)
            assertEquals(3 * x2 - (x2 * x2) / 4, result2, 1e-8) { "calculating: $input" }
        } else {
            fail("expression cannot be parsed")
        }
    }


    //TODO ReierAsk this shoould not fail
    @Test
    fun advancedMultiVariableExpressionTest() {
        val input = "(x^3 * y - 2.5 * x * y^2 + 10) / (x^2 + y^2 + 1)"

        val x1 = 3.0
        val y1 = 2.0
        val x2 = 5.0
        val y2 = 1.5

        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val expr = Expr(calculator, input)

        expr.trace(Trace.ALL)
        if (expr.parse()) {
            calculator.variables.add(x1)
            calculator.variables.add(y1)

            logger.info("first calculation")
            expr.calculate()
            val result1 = calculator.result()
            assertNotNull(result1)

            val expected1 = (Math.pow(x1, 3.0) * y1 - 2.5 * x1 * Math.pow(y1, 2.0) + 10) / (Math.pow(x1, 2.0) + Math.pow(y1, 2.0) + 1)
            assertEquals(expected1, result1, 1e-8) { "Feil for x=$x1, y=$y1" }

            calculator.variables[0] = x2
            calculator.variables[1] = y2

            logger.info("second calculation")
            expr.calculate()
            val result2 = calculator.result()
            assertNotNull(result2)

            val expected2 = (Math.pow(x2, 3.0) * y2 - 2.5 * x2 * Math.pow(y2, 2.0) + 10) / (Math.pow(x2, 2.0) + Math.pow(y2, 2.0) + 1)
            assertEquals(expected2, result2, 1e-8) { "Feil for x=$x2, y=$y2" }
        } else {
            fail("could no parse the expression: $input")
        }
    }

    @Test
    fun weirdParenthesisAndMultiVariableMonsterTest() {
      //  val input = "(( ( (x ^ 2) + (y ^ 2) ) / ( (x - y) ^ 2 + 1 ) ) * ( (z) - (1) ) ) + ( ( (2.5) * x ) / ( y ) )"
        val input = "((((x^2)+(y^2))/((x-y)^2+1))*((z)-(1)))+(((2.5)*x)/(y))"
        val xVal = 3.0
        val yVal = 2.0
        val zVal = 5.0

        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val expr = Expr(calculator, input)

        expr.rotateTraceLevel()

        if (expr.parse()) {

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
      //  val input = "gyp(4; 3)" // Math.sqrt(x * x - y * y)
     //   val input = "gyp(4 + 3; 2)"
        val input = "3 + 2"
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)

        val expr = Expr(calculator, input)
        expr.trace(Trace.ALL)
        val parsed = expr.parse()
        assertTrue(parsed)
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
        val expr = Expr(calculator, input)

        val parsed = expr.parse()
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
        val expr = Expr(calculator, input)

        val parsed = expr.parse()
        assertTrue(parsed)

        expr.calculate()
        val result = calculator.result()
        logger.info("Resultat: $result")
    }

    @Test
    fun OperatorOrderTest() {
        val input = "5*2^2"

        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val expr = Expr(calculator, input)

        val parsed = expr.parse()
        assertTrue(parsed)

        expr.calculate()
        val result = calculator.result()
        logger.info("Resultat: $result")
    }

    @Test
    fun strangeNumberTest() {
        val input = "5e0 * 2.5"

        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val expr = Expr(calculator, input)

        val parsed = expr.parse()
        assertFalse(parsed)
    }

    @Test
    fun variablesMissingOperatorTest() {
        val input = "1 + xy"
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val expr = Expr(calculator, input)
        expr.trace(Trace.ALL)
        val parsed = expr.parse()
        assertFalse(parsed)
    }

    @Test
    fun missingMultiplicatorTest() {
        val input = "3x"
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val expr = Expr(calculator, input)
        expr.trace(Trace.ALL)
        val parsed = expr.parse()
        assertFalse(parsed)
    }

    @Test
    fun missingMultiplicatorTest2() {
        val input = "3(x+2)"
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val expr = Expr(calculator, input)
        val parsed = expr.parse()
        assertFalse(parsed)
    }

    @Test
    fun remainingExpressionTest() {
        val input = "y x + sin(90)"
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val expr = Expr(calculator, input)
        val parsed = expr.parse()
        assertFalse(parsed)
    }

    @Test
    fun remainingExpressionRelaxedTest() {
        val x = 4.0
        val y = 2.0
        val input = "y x + sin(90)"
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val expr = Expr(calculator, input)
        expr.relaxed = true
        val parsed = expr.parse()
        assertTrue(parsed)

        calculator.variables.add(x)
        calculator.variables.add(y)
        expr.calculate()
        val result = calculator.allResults()
        assertEquals(2, result.size)
        assertEquals(2.0, result.first(), 1e-10)
        assertEquals(5.0, result.last(), 1e-10)
    }

    @Test
    fun separatedRelaxedExpressionsTest() {
        val x = 4.0
        val input = "x+3; 3+3" // interpreted as two expressions
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val expr = Expr(calculator, input)
        expr.relaxed = true
        val parsed = expr.parse()
        assertTrue(parsed)

        calculator.variables.add(x)
        expr.calculate()
        val result = calculator.allResults()
        assertEquals(2, result.size)
        assertEquals(7.0, result.first(), 1e-10)
        assertEquals(6.0, result.last(), 1e-10)
    }

    @Test
    fun separatedExpressionsTest() {
        val input = "x+3;sin(90)" // interpreted as two expressions
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val expr = Expr(calculator, input)
        val parsed = expr.parse()
        assertFalse(parsed)
    }

    @Test
    fun wrongNumberOfBasicOperatorArgumentsTest() {
        val input = "x+3 3+3" //interpreted as ekstra arguments to the plus operator
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val expr = Expr(calculator, input)
        val parsed = expr.parse()
        assertFalse(parsed)
    }

    @Test
    fun separatorTest() {
        val x = 4.0
        val y = 2.0
        val input = "gyp (x ; y )"
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val expr = Expr(calculator, input)
        val parsed = expr.parse()
        assertTrue(parsed)

        calculator.variables.add(x)
        calculator.variables.add(y)
        expr.calculate()
        assertEquals(sqrt(x * x - y * y), calculator.result()!!, 1e-10)
    }

    @Test
    fun standAloneSeparatorTest() {
        val input = "3 ;"
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val expr = Expr(calculator, input)
        val parsed = expr.parse()
        assertFalse(parsed)
    }

    @Test
    fun singleOperatorTest() {
        val input = "pi"
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val expr = Expr(calculator, input)
        expr.trace(Trace.OFF)
        val parsed = expr.parse()
        assertTrue(parsed)
        expr.calculate()
        logger.info("Result=${calculator.result()}")
    }

    @Test
    fun invalidExpressionTest() {
        val input = "3+pig*5"
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val expr = Expr(calculator, input)
        val parsed = expr.parse()
        assertFalse(parsed)
    }

    @Test
    fun MissingRightParenthesisTest() {
        val input = "3 * (3 + x"
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val expr = Expr(calculator, input)
        val parsed = expr.parse()
        assertFalse(parsed)
    }

    @Test
    fun MissingLeftParenthesisTest() {
        val input = "3 + x) * 3"
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val expr = Expr(calculator, input)
        val parsed = expr.parse()
        assertFalse(parsed)
    }

    @Test
    fun UnbalancedExpressionTest() {
        val input = "3 + x) * 3("
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val expr = Expr(calculator, input)
        val parsed = expr.parse()
        assertFalse(parsed)
    }

    @Test
    fun UnbalancedExpressionTest2() {
        val input = ")sin(x)) * 3*(2("
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val expr = Expr(calculator, input)
        val parsed = expr.parse()
        assertFalse(parsed)
    }

    @Test
    fun TwoExpressions() {
        val x = 2.0
        val y = 1.0
        val input = "x+3 sin(y)"
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        calculator.variables.add(x)
        calculator.variables.add(y)
        val expr = Expr(calculator, input)
        val parsed = expr.parse()
        assertTrue(parsed)
        expr.calculate()
        logger.info("Result: ${calculator.result()}")
    }

    @Test
    fun TwoExpressions2() {
        val x = 2.0
        val y = 1.0
        val input = "x+3 3+3"
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        calculator.variables.add(x)
        calculator.variables.add(y)
        val expr = Expr(calculator, input)
        val parsed = expr.parse()
        assertTrue(parsed)
        expr.calculate()
        logger.info("Result: ${calculator.result()}")
    }

    @Test
    fun TwoExpressions3() {
        val x = 2.0
        val y = 1.0
        val input = "x+3;3+3"
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        calculator.variables.add(x)
        calculator.variables.add(y)
        val expr = Expr(calculator, input)
        val parsed = expr.parse()
        assertTrue(parsed)
        expr.calculate()
        logger.info("Result: ${calculator.result()}")
    }

}