package com.techreier.edrops.calc

import com.techreier.edrops.graph.logger
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import kotlin.test.assertEquals
import kotlin.test.fail

class ExprTest {

    val calc = CalcDouble(Double::class.javaObjectType)

    @Test
    fun happyTest() {
        val input = "sin(90)"
        val expr = Expr(calc, input)
        if (expr.parse()) expr.calculate()
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
        if (expr.parse()) expr.calculate()
        val singleResult = calc.result()
        logger.info("Result = $singleResult")
        assertNotNull(singleResult)
        assertEquals(1.0, singleResult, 1e-9)
        assertEquals(1, calc.stack.size, "Single result is not returned")
    }

    @Test
    fun twoArgumentFunctionTest() {
        val input = "hyp(3;4)"
        val expr = Expr(calc, input)
        if (expr.parse()) expr.calculate()
        val singleResult = calc.result()
        logger.info("Result = $singleResult")
        assertNotNull(singleResult)
        assertEquals(5.0, singleResult, 1e-9)
        assertEquals(1, calc.stack.size, "Single result is not returned")
    }

    @Test
    fun twoArgumentFunctionRightOrderTest() {
        val x = 4.0;
        val y = 3.0;
        calc.variables.add(x)
        calc.variables.add(y)
        val input = "gyp( x ; y )"
        val expr = Expr(calc, input)
        if (expr.parse()) expr.calculate()
        val singleResult = calc.result()
        logger.info("Result = $singleResult")
        assertNotNull(singleResult)
        assertEquals(2.6457513110645907, singleResult, 1e-9)
        assertEquals(1, calc.stack.size, "Single result is not returned")
    }

    @Test
    fun twoArgumentFunctionWrongOrderTest() {
        val x = 3.0;
        val y = 4.0;
        calc.variables.add(x)
        calc.variables.add(y)
        val input = "gyp(x;y)"
        val expr = Expr(calc, input)
        if (expr.parse()) expr.calculate()
        val singleResult = calc.result()
        logger.info("Result = $singleResult")
        assertNotNull(singleResult)
        assertEquals(Double.NaN, singleResult)
        assertEquals(1, calc.stack.size, "Single result is not returned")
    }

    @Test
    fun happyVariableTest() {
        val x = 90.0
        calc.variables.add(x)
        val input = "sin(x)"
        val expr = Expr(calc, input)
        if (expr.parse()) expr.calculate()
     //   val singleResult = calc.stack.peekFirst()
        val singleResult = calc.result()
        logger.info("Result = $singleResult")
        assertNotNull(singleResult)
        assertEquals(1.0, singleResult, 1e-9)
        assertEquals(1, calc.stack.size, "Single result is not returned")
    }

    @Test
    fun functionInfunctionTest() {
        val x = 90.0
        calc.variables.add(x)
        val input = "ln(sin(x))"
        val expr = Expr(calc, input)
        if (expr.parse()) expr.calculate()
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
        if (expr.parse()) expr.calculate()
        val singleResult = calc.result()
        logger.info("Result = $singleResult")
        assertNotNull(singleResult)
        assertEquals(Double.NEGATIVE_INFINITY, singleResult)
        assertEquals(1, calc.stack.size, "Single result is not returned")
    }

    @Test
    fun calculateSeriesTest() {
        val input = "sin(x)"
        val x1 = 0.0
        val x2 = 90.0
        val expr = Expr(calc, input)
        logger.info("calculating with x1")
        if (expr.parse()) {
            calc.variables.add(x1)
            expr.calculate()
            val result1 = calc.result()
            assertNotNull(result1)
            assertEquals(0.0, result1, 1e-8, "calculating: " + input)

            logger.info("calculating with x2")
            calc.variables.add(x2)
            expr.calculate()
            val result2 = calc.result()
            assertNotNull(result2)
            assertEquals(1.0, result2, 1e-8, "calculating: " + input)
        } else {
           fail("expression cannot be parsed")
        }
    }
}