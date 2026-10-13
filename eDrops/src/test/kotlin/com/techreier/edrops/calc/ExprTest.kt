package com.techreier.edrops.calc

import com.techreier.edrops.graph.logger
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ExprTest {

    val calc = CalcDouble(Double::class.javaObjectType, true)

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
        val input = "log(sin(x))"
        val expr = Expr(calc, input)
        if (expr.parse()) expr.calculate()
        val singleResult = calc.stack.peekFirst()
        logger.info("Result = $singleResult")
        assertEquals(1.0, singleResult, 1e-9)
        assertEquals(1, calc.stack.size, "Single result is not returned")
    }
}