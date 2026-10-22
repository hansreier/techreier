package com.techreier.edrops.calc

import com.techreier.edrops.graph.logger
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import kotlin.math.pow
import kotlin.test.assertEquals

class ExprTest {

    val calc = CalcDouble()

    @Test
    fun happyTest() {
        val input = "sin(90)"
        val expr = Expr(calc, input)
        calc.trace = Trace.ALL
        expr.parse()
        expr.calculate()
        val singleResult = calc.result()
        logger.info("Result = $singleResult")
        assertNotNull(singleResult)
        assertEquals(1.0, singleResult, 1e-9)
        assertEquals(1, calc.stack.size, "Single result is not returned")
    }

    @Test
    fun simpleFunctionTest() {
        val input = "sin 90"
        val expr = Expr(calc, input)
        expr.parse()
        expr.calculate()
        val singleResult = calc.result()
        logger.info("Result = $singleResult")
        assertNotNull(singleResult)
        assertEquals(1.0, singleResult, 1e-9)
        assertEquals(1, calc.stack.size, "Single result is not returned")
    }

    @Test
    fun twoArgumentFunctionTest() {
        calc.trace = Trace.ALL
        val input = "hyp(3;4)"
        val expr = Expr(calc, input)
        expr.parse()
        expr.calculate()
        val singleResult = calc.result()
        logger.info("Result = $singleResult")
        assertNotNull(singleResult)
        assertEquals(5.0, singleResult, 1e-9)
        assertEquals(1, calc.stack.size, "Single result is not returned")
    }

    @Test
    fun doubleXFunctionTest() {
        val input = "hyp(x  ; x)"
        calc[Op.X] = 2.0
        val expr = Expr(calc, input)
        expr.parse()
        expr.calculate()
        val singleResult = calc.result()
        logger.info("Result = $singleResult")
        assertNotNull(singleResult)
        assertEquals(2.8284271247461903, singleResult, 1e-9)
        assertEquals(1, calc.stack.size, "Single result is not returned")
    }

    @Test
    fun twoArgumentFunctionRightOrderTest() {
        calc[Op.X] = 4.0
        calc[Op.Y] = 3.0
        val input = "gyp( x ; y )"
        val expr = Expr(calc, input)
        expr.parse()
        expr.calculate()
        val singleResult = calc.result()
        logger.info("Result = $singleResult")
        assertNotNull(singleResult)
        assertEquals(2.6457513110645907, singleResult, 1e-9)
        assertEquals(1, calc.stack.size, "Single result is not returned")
    }

    @Test
    fun twoArgumentFunctionWrongOrderTest() {
        calc[Op.X] = 3.0
        calc[Op.Y] = 4.0
        val input = "gyp(x;y)"
        val expr = Expr(calc, input)
        expr.parse()
        expr.calculate()
        val singleResult = calc.result()
        logger.info("Result = $singleResult")
        assertNotNull(singleResult)
        assertEquals(Double.NaN, singleResult)
        assertEquals(1, calc.stack.size, "Single result is not returned")
    }

    @Test
    fun happyVariableTest() {
        calc[Op.X] = 90.0
        val input = "sin(x)"
        val expr = Expr(calc, input)
        expr.parse()
        expr.calculate()
        val singleResult = calc.result()
        logger.info("Result = $singleResult")
        //   assertNotNull(singleResult)
        assertEquals(1.0, singleResult, 1e-9)
        assertEquals(1, calc.stack.size, "Single result is not returned")
    }

    @Test
    fun functionInfunctionTest() {
        calc[Op.X] = 90.0
        val input = "ln(sin(x))"
        val expr = Expr(calc, input)
        expr.parse()
        expr.calculate()
        val singleResult = calc.result()
        logger.info("Result = $singleResult")
        assertNotNull(singleResult)
        assertEquals(0.0, singleResult, 1e-9)
        assertEquals(1, calc.stack.size, "Single result is not returned")
    }

    @Test
    fun infinityTest() {
        val input = "ln(sin(180))"
        val expr = Expr(calc, input)
        calc.trace = Trace.ALL
        expr.parse()
        expr.calculate()
        val singleResult = calc.result()
        logger.info("Result = $singleResult")
        assertNotNull(singleResult)
        assertEquals(Double.NEGATIVE_INFINITY, singleResult)
        assertEquals(1, calc.stack.size, "Single result is not returned")
    }

    @Test
    fun calculateSeriesTest() {
        val input = "sin(x)"
        val expr = Expr(calc, input)
        logger.info("calculating with x1")
        expr.parse()
        calc[Op.X] = 0.0
        expr.calculate()
        val result1 = calc.result()
        assertNotNull(result1)
        assertEquals(0.0, result1, 1e-8, "calculating: " + input)

        logger.info("calculating with x2")
        calc[Op.X] = 90.0
        expr.calculate()
        val result2 = calc.result()
        assertNotNull(result2)
        assertEquals(1.0, result2, 1e-8, "calculating: " + input)

    }

    @Test
    fun operatorsSameLevelTest() {
        calc[Op.X] = 8.0
        calc[Op.Y] = 6.0
        val input = "3 + x - y - 3"
        val expr = Expr(calc, input)
        calc.trace = Trace.ALL
        expr.parse()
        logger.info("parse ok, can be calculated")
        expr.calculate()
        assertEquals(2.0, calc.result())
    }

    @Test
    fun operatorsDifferentLevelTest() {
        val input = "3 + 5 * 4 + 1"
        val expr = Expr(calc, input)
        calc.trace = Trace.ALL
        expr.parse()
        logger.info("parse ok, can be calculated")
        expr.calculate()
        calc.result()
    }

    @Test
    fun operatorsAndFunctionTest() {
        val input = "3 + sin(9 * 10)^2 * 2 + 1"
        val expr = Expr(calc, input)
        calc.trace = Trace.ALL
        expr.parse()
        logger.info("parse ok, can be calculated")
        expr.calculate()
        val result = calc.result()
        assertEquals(6.0, result, 1e-8, "calculation: " + input)
    }

    @Test
    fun repeatedMultiplyTest() {
        val input = "5 * 3 * 4"
        val expr = Expr(calc, input)
        calc.trace = Trace.ALL
        expr.parse()
        logger.info("parse ok, can be calculated")
        expr.calculate()
        val result = calc.result()
        assertEquals(60.0, result, 1e-8, "calculation: " + input)
    }

    @Test
    fun priorityTest() {
        val input = "5 + 3 * 4"
        val expr = Expr(calc, input)
        calc.trace = Trace.ALL
        expr.parse()
        logger.info("parse ok, can be calculated")
        expr.calculate()
        val result = calc.result()
        assertEquals(17.0, result, 1e-8, "calculation: " + input)
    }

    @Test
    fun repeatedAddTest() {
        calc[Op.X] = 8.0
        calc[Op.Y] = 6.0
        calc[Op.Z] = 4.0
        val input = "x + y + z"
        val expr = Expr(calc, input)
        calc.trace = Trace.ALL
        expr.parse()
        expr.calculate()
        val result = calc.result()
        assertEquals(18.0, result, 1e-8, "calculation: " + input)
    }

    @Test
    fun withParanthesisTest() { //TODO Fails

        val input = "(3 + 5 * (2 + 3) )/ 2"
        val expr = Expr(calc, input)
        expr.parse()
        expr.calculate()
        val result = calc.result()

        assertEquals(14.0, result, 1e-8)
    }

    @Test
    fun weirdParenthesisAndMultiVariableMonsterTest() {
        val x = 3.0
        val y = 6.0
        val z = 5.0
        calc[Op.X] = x
        calc[Op.Y] = y
        calc[Op.Z] = z

        val input = "(( ( (x ^ 2) + (y ^ 2) ) / ( (x - y) ^ 2 + 1 ) ) * ( (z) - (1) ) ) + ( ( (2.5) * x ) / ( y ) )"

        val expr = Expr(calc, input)
        calc.trace = Trace.ALL
        expr.parse()
        expr.calculate()
        val result = calc.result()

        val term1 = (x.pow(2.0) + y.pow(2.0)) / ((x - y).pow(2.0) + 1.0)
        val term2 = z - 1.0
        val term3 = (2.5 * x) / y
        val expected = (term1 * term2) + term3

        assertEquals(expected, result, 1e-8)
    }

}