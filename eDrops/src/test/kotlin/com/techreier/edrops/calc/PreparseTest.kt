package com.techreier.edrops.calc

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class PreparseTest {

    @Test
    fun happyTest() {
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val input = "3.0(x)"

        val expr = Expr(calculator, input)
        val parsed = expr.preparse()
        assertTrue(parsed)

    }

    @Test
    fun expressionNotFoundTest() {
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val input = "Bullshit"

        val expr = Expr(calculator, input)
        val parsed = expr.preparse()
        assertFalse(parsed)

    }

}

