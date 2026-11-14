package com.techreier.edrops.calc

import com.techreier.edrops.util.Trace
import com.techreier.edrops.exceptions.ParseException
import com.techreier.edrops.util.Trace.debug
import com.techreier.edrops.util.Trace.level
import com.techreier.edrops.util.TraceLevel
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.math.pow
import kotlin.test.assertEquals

class ExprVariableTest {

    lateinit var calc: CalcDouble

    @BeforeEach
    fun setUp() {
        calc = CalcDouble()
        level = TraceLevel.INFO
        debug("")
    }

    @Test
    fun happySingleVariableTest() {
        val input = "x"
        val expr = Expr(calc, input)
        expr[Op.X] = 90.0
        expr.parse()
        expr.calculate()
        val singleResult = calc.result()
        assertEquals(90.0, singleResult, 1e-9)
    }

    @Test
    fun notExistingVariableTest() {
        calc[Op.ADD] = 90.0
        calc[Op.SIN] = 90.0
        val input = "sin(x)"
        val expr = Expr(calc, input)
        val e = assertThrows<ParseException> {
            expr.parse()
        }
        assertEquals(ILLEGAL_VARIABLES, e.parseError.key)
        assertEquals("ADD SIN", e.parseError.oper)
        Trace.info("Result = ${e.message} ${e.parseError} ")
    }

    @Test
    fun operatorsSameLevelTest() {
        calc[Op.X] = 8.0
        calc[Op.Y] = 6.0
        val input = "3 + x - y - 3"
        val expr = Expr(calc, input)
        expr.parse()
        expr.calculate()
        assertEquals(2.0, calc.result())
    }

    @Test
    fun repeatedAddTest() {
        calc[Op.X] = 8.0
        calc[Op.Y] = 6.0
        calc[Op.Z] = 4.0
        val input = "x + y + z"
        val expr = Expr(calc, input)
        expr.parse()
        expr.calculate()
        val result = calc.result()
        assertEquals(18.0, result, 1e-8, "calculation: " + input)
    }

    @Test
    fun weirdParenthesisAndMultiVariableMonsterTest() {
        val x = 3.0
        val y = 6.0
        val z = 5.0
        calc.setAll(mapOf(Op.X to x, Op.Y to y, Op.Z to z))

        val input = "(( ( (x ^ 2) + (y ^ 2) ) / ( (x - y) ^ 2 + 1 ) ) * ( (z) - (1) ) ) + ( ( (2.5) * x ) / ( y ) )"

        val expr = Expr(calc, input)
        expr.parse()
        expr.calculate()
        val result = calc.result()

        val term1 = (x.pow(2.0) + y.pow(2.0)) / ((x - y).pow(2.0) + 1.0)
        val term2 = z - 1.0
        val term3 = (2.5 * x) / y
        val expected = (term1 * term2) + term3

        assertEquals(expected, result, 1e-8)
    }

    @Test
    fun simplePowerTest() {
        val x = 2.0
        val y = 3.0
        calc.setAll(mapOf(Op.X to x, Op.Y to y))

        val input = "x^y"

        val expr = Expr(calc, input)
        expr.parse()
        expr.calculate()
        assertEquals(8.0, calc.result() )
    }

    @Test
    fun powerPriorityTest() {
        val x = 2.0
        val y = 3.0
        val z = 1.0
        val input = "x^y^z"

        val expr = Expr(calc, input)
        expr.setAll(mapOf(Op.X to x, Op.Y to y, Op.Z to z))
        expr.parse()
        expr.calculate()
        assertEquals(8.0, expr.result() )
    }

    @Test
    fun powerPriorityTest2() {
        val x = 2.0
        val y = 3.0
        val z = 1.0
        val input = "x^(y - 1*2)^z"

        val expr = Expr(calc, input)
        expr.setAll(mapOf(Op.X to x, Op.Y to y, Op.Z to z))
        expr.parse()
        expr.calculate()
        assertEquals(2.0, expr.result() )
    }

    @Test
    fun priorityTest3() {
        val x = 5.0
        val y = 2.0
        val z = 3.0
        val input = "x + 2 * y^3^2 - 12 / z"

        val expr = Expr(calc, input)
        expr.setAll(mapOf(Op.X to x, Op.Y to y, Op.Z to z))
        expr.parse()
        expr.calculate()
        assertEquals(1025.0, expr.result() )
    }

    @Test
    fun negativeVariableTest() {
        val input = "-x"
        val expr = Expr(calc, input)
        expr[Op.X] = 2.0
        expr.parse()
        expr.calculate()
        assertEquals(-2.0, expr.result(), 1e-9 )
    }

    @Test
    fun multipleNegateAndSubtractTest() {
        val calc = CalcDouble()
        val expr = Expr(calc, "-x - -y")
        expr[Op.X] = 10.0
        expr[Op.Y] = 4.0
        expr.parse()
        expr.calculate()
        assertEquals(-6.0, calc.result())
    }

}
