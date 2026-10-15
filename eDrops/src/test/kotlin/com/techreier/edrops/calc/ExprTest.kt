package com.techreier.edrops.calc

import com.techreier.edrops.graph.logger
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ExprTest {

    val calc = CalcDouble(Double::class.javaObjectType)

    @Test
    fun happyTest() {
        val input = "sin(90)"
        val expr = Expr(calc, input)
        if (expr.parse()) expr.calculate()
        val singleResult = calc.stack.peekFirst()
        logger.info("Result = $singleResult")
        assertEquals(1.0, singleResult, 1e-9)
        assertEquals(1, calc.stack.size, "Single result is not returned")
    }

    @Test
    fun simpleFunctionTest() {
        val input = "sin 90"
        val expr = Expr(calc, input)
        if (expr.parse()) expr.calculate()
        val singleResult = calc.stack.peekFirst()
        logger.info("Result = $singleResult")
        assertEquals(1.0, singleResult, 1e-9)
        assertEquals(1, calc.stack.size, "Single result is not returned")
    }

    @Test
    fun twoArgumentFunctionTest() {
        val input = "hyp(3;4)"
        val expr = Expr(calc, input)
        if (expr.parse()) expr.calculate()
        val singleResult = calc.stack.peekFirst()
        logger.info("Result = $singleResult")
        assertEquals(5.0, singleResult, 1e-9)
        assertEquals(1, calc.stack.size, "Single result is not returned")
    }

    @Test
    fun twoArgumentFunctionRightOrderTest() {
        val input = "gyp(4;3)"
        val expr = Expr(calc, input)
        if (expr.parse()) expr.calculate()
        val singleResult = calc.stack.peekFirst()
        logger.info("Result = $singleResult")
        assertEquals(2.6457513110645907, singleResult, 1e-9)
        assertEquals(1, calc.stack.size, "Single result is not returned")
    }

    @Test
    fun twoArgumentFunctionWrongOrderTest() {
        val input = "gyp(3;4)"
        val expr = Expr(calc, input)
        if (expr.parse()) expr.calculate()
        val singleResult = calc.stack.peekFirst()
        logger.info("Result = $singleResult")
        assertEquals(Double.NaN, singleResult)
        assertEquals(1, calc.stack.size, "Single result is not returned")
    }

    @Test
    fun happyVariableTest() {
        calc.stack.push(90.0)
        calc.saveVarsFromStack()
        val input = "sin(x)"
        val expr = Expr(calc, input)
        if (expr.parse()) expr.calculate()
        val singleResult = calc.stack.peekFirst()
        logger.info("Result = $singleResult")
        assertEquals(1.0, singleResult, 1e-9)
        assertEquals(1, calc.stack.size, "Single result is not returned")
    }

    @Test
    fun functionInfunctionTest() {
        calc.stack.push(90.0)
        calc.saveVarsFromStack()
        val input = "ln(sin(x))"
        val expr = Expr(calc, input)
        if (expr.parse()) expr.calculate()
        val singleResult = calc.stack.peekFirst()
        logger.info("Result = $singleResult")
        assertEquals(0.0, singleResult, 1e-9)
        assertEquals(1, calc.stack.size, "Single result is not returned")
    }

    @Test
    fun infinityTest() {
        val input = "ln(sin(180))"
        val expr = Expr(calc, input)
        calc.trace = Trace.ALL
        if (expr.parse()) expr.calculate()
        val singleResult = calc.stack.peekFirst()
        logger.info("Result = $singleResult")
        assertEquals(Double.NEGATIVE_INFINITY, singleResult)
        assertEquals(1, calc.stack.size, "Single result is not returned")
    }
}