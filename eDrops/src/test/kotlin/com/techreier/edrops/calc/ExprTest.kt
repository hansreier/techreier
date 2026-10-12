package com.techreier.edrops.calc

import org.junit.jupiter.api.Test

class ExprTest {

    val calc = CalcDouble(Double::class.javaObjectType, true)

    @Test
    fun happyTest() {
        val input = "sin(30)"
        val expr = Expr(calc, input)
        if (expr.parse()) expr.calculate()
    }
}