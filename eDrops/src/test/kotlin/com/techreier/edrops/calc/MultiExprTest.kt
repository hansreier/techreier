package com.techreier.edrops.calc

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MultiExprTest {

    val calc = CalcDouble()

    @Test
    fun happyTest() {

        val input = """
            x = 5;
            y = 3 * x;
            z = x + y;
        """.trimIndent()

        val expr = MultiExpr(calc, input)
        expr.parse()
        expr.calculate()

        assertEquals(5.0, expr[Op.X])
        assertEquals(15.0, expr[Op.Y])
        assertEquals(20.0, expr[Op.Z])
    }
}