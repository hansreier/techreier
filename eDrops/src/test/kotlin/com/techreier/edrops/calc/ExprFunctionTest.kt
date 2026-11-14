package com.techreier.edrops.calc

import com.techreier.edrops.exceptions.ParseException
import com.techreier.edrops.util.Trace.debug
import com.techreier.edrops.util.Trace.level
import com.techreier.edrops.util.TraceLevel
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.junit.jupiter.api.assertThrows
import kotlin.Double.Companion.NaN
import kotlin.test.assertEquals

class ExprFunctionTest {

    lateinit var calc: CalcDouble

    @BeforeEach
    fun setUp() {
        calc = CalcDouble()
        // Trigonometric tests expect degrees
        calc.radians = false
        level = TraceLevel.INFO
        debug("")
    }

    @Test
    fun happyTest() {
        val input = "sin(90)"
        val expr = Expr(calc, input)
        expr.parse()
        expr.calculate()
        val singleResult = calc.result()
        assertNotNull(singleResult)
        assertEquals(1.0, singleResult, 1e-9)
        assertEquals(1, calc.stack.size, "Single result is not returned")
    }

    @Test
    fun simpleFunctionTest() {
        val input = "sin(90)"
        val expr = Expr(calc, input)
        expr.parse()
        expr.calculate()
        val singleResult = expr.result()
        assertNotNull(singleResult)
        assertEquals(1.0, singleResult, 1e-9)
        assertEquals(1, calc.stack.size, "Single result is not returned")
    }

    @Test
    fun simpleFunctionsTest() {
        val input = "sin(90) + cos(0)"
        val expr = Expr(calc, input)
        expr.parse()
        expr.calculate()
        val singleResult = expr.result()
        assertNotNull(singleResult)
        assertEquals(2.0, singleResult, 1e-9)
        assertEquals(1, calc.stack.size, "Single result is not returned")
    }

    @Test
    fun twoArgumentFunctionTest() {
        val input = "hyp(3;4)"
        val expr = Expr(calc, input)
        expr.parse()
        expr.calculate()
        val singleResult = calc.result()
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
        assertNotNull(singleResult)
        assertEquals(NaN, singleResult)
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
        assertEquals(1.0, singleResult, 1e-9)
        assertEquals(1, calc.stack.size, "Single result is not returned")
    }

    @Test
    fun functionInFunctionTest() {
        calc[Op.X] = 90.0
        val input = "ln(sin(x))"
        val expr = Expr(calc, input)
        expr.parse()
        expr.calculate()
        val singleResult = calc.result()
        assertNotNull(singleResult)
        assertEquals(0.0, singleResult, 1e-9)
        assertEquals(1, calc.stack.size, "Single result is not returned")
    }

    @Test
    fun infinityTest() {
        val input = "ln(sin(180))"
        val expr = Expr(calc, input)
        expr.parse()
        expr.calculate()
        val singleResult = calc.result()
        assertNotNull(singleResult)
        assertEquals(Double.NEGATIVE_INFINITY, singleResult)
        assertEquals(1, calc.stack.size, "Single result is not returned")
    }

    @Test
    fun calculateSeriesTest() {
        val input = "sin(x)"
        val expr = Expr(calc, input)
        debug("calculating with x1")
        expr.parse()
        calc[Op.X] = 0.0
        expr.calculate()
        val result1 = calc.result()
        assertNotNull(result1)
        assertEquals(0.0, result1, 1e-8, "calculating: " + input)

        debug("calculating with x2")
        calc[Op.X] = 90.0
        expr.calculate()
        val result2 = calc.result()
        assertNotNull(result2)
        assertEquals(1.0, result2, 1e-8, "calculating: " + input)

    }

    @Test
    fun operatorsAndFunctionTest() {
        val input = "3 + sin(9 * 10)^2 * 2 + 1"
        val expr = Expr(calc, input)
        expr.parse()
        expr.calculate()
        val result = calc.result()
        assertEquals(6.0, result, 1e-8, "calculation: " + input)
    }

    @Test
    fun priorityTest() {
        val input = "sin(90) + 3 * 4^2"
        val expr = Expr(calc, input)
        expr.parse()
        expr.calculate()
        val result = calc.result()
        assertEquals(49.0, result, 1e-8, "calculation: " + input)
    }

    @Test
    fun powerSinTest() {
        val input = "2^sin(90)"
        val expr = Expr(calc, input)
        expr.parse()
        expr.calculate()
        assertEquals(2.0, expr.result(), 1E-9 )
    }

    @Test
    fun basicAndFunctionTest() {
        val input = "sin(x) + cos(x - 90)"
        val expr = Expr(calc, input)
        expr[Op.X] = 90.0
        expr[Op.Y] = 90.0
        expr.parse()
        expr.calculate()
        val result = expr.result()

        assertEquals(2.0, result, 1e-8)
    }

    @Test
    fun negateFunctionTest() {
        val expr = Expr(calc, "-sin(x) + cos(y)")
        expr[Op.X] = 90.0
        expr[Op.Y] = 0.0
        expr.parse()
        expr.calculate()
        assertEquals(0.0, calc.result())
    }

    @Test
    fun functionNegateTest() {
        val expr = Expr(calc, "NEGATE -2")
        expr.parse()
        expr.calculate()
        assertEquals(2.0 , calc.result())
    }

    @Test
    fun negateTest() {
        val input = "negate(-3)"
        val expr = Expr(calc, input)
        expr.parse()
        expr.calculate()
        assertEquals(3.0, expr.result())
    }

    @Test
    fun constantTest() {
        calc.radians = true
        val input = "sin(pi)"
        val expr = Expr(calc, input)
        expr.parse()
        expr.calculate()
        assertEquals(0.0, expr.result(), 1e-9)
    }

    @Test
    fun randomTest() {
        val input = "10 random"
        val expr = Expr(calc, input)
        expr.parse()
        expr.calculate()
        assertThat(expr.result()).isBetween(0.0, 10.0)
    }

    @Test
    fun sqrtSqrTest() {
        val input = "sqrt(sqr(2))"
        val expr = Expr(calc, input)
        expr.parse()
        expr.calculate()
        assertEquals(2.0, expr.result(), 1e-9)
    }

    @Test
    fun log10Pow10Test() {
        val input = "log10(pow10(2))"
        val expr = Expr(calc, input)
        expr.parse()
        expr.calculate()
        assertEquals(2.0, expr.result(), 1e-9)
    }

    @Test
    fun sinAsinTest() {
        val input = "asin(sin(35))"
        val expr = Expr(calc, input)
        expr.parse()
        expr.calculate()
        assertEquals(35.0, expr.result(), 1e-9)
    }

    @Test
    fun cosAcosTest() {
        val input = "acos(cos(35))"
        val expr = Expr(calc, input)
        expr.parse()
        expr.calculate()
        assertEquals(35.0, expr.result(), 1e-9)
    }

    @Test
    fun tanAtanTest() {
        val input = "atan(tan(35))"
        val expr = Expr(calc, input)
        expr.parse()
        expr.calculate()
        assertEquals(35.0, expr.result(), 1e-9)
    }

    @Test
    fun absSignTest() {
        val input = "sign(abs(-3))"
        val expr = Expr(calc, input)
        expr.parse()
        expr.calculate()
        assertEquals(1.0, expr.result(), 1e-9)
    }

    @Test
    fun radDegTest() {
        val input = "deg(rad(90))"
        val expr = Expr(calc, input)
        expr.parse()
        expr.calculate()
        assertEquals(90.0, expr.result(), 1e-9)
    }

    @Test
    fun expLnTest() {
        val input = "exp(ln(55000))"
        val expr = Expr(calc, input)
        expr.parse()
        expr.calculate()
        assertEquals(55000.0, expr.result(), 1e-9)
    }

    @Test
    fun sumTest() {
        val input = "sum(1; 2; 3; 4; 5)"
        val expr = Expr(calc, input)
        expr.parse()
        expr.calculate()
        assertEquals(15.0, expr.result(), 1e-9)
    }

    @Test
    fun sumAdvancedExpressionTest() {
        val input = "sum(sin(90); 2; cos(0))"
        val expr = Expr(calc, input)
        expr.parse()
        expr.calculate()
        assertEquals(4.0, expr.result(), 1e-9)
    }

    @Test
    fun sumGiganticExpressionTest() {
        val input = "sum(max(sin(90);2;3) + 2; 2; cos(0))"
        val expr = Expr(calc, input)
        expr.parse()
        expr.calculate()
        assertEquals(8.0, expr.result(), 1e-9)
    }

    @Test
    fun hypWrongNoOfArgumentsTest() {
        val input = "hyp(1; 2; 3)"
        val expr = Expr(calc, input)
        val e = assertThrows <ParseException> { expr.parse() }
        assertEquals(WRONG_ARGUMENT_COUNT, e.parseError.key)
    }

}
