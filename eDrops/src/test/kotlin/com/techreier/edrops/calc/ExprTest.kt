package com.techreier.edrops.calc

import com.techreier.edrops.exceptions.CalcException
import com.techreier.edrops.exceptions.ParseException
import com.techreier.edrops.graph.logger
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.junit.jupiter.api.assertThrows
import kotlin.math.pow
import kotlin.test.assertEquals

class ExprTest {

    val calc = CalcDouble()

    @Test
    fun happy42Test() {
        val input = "42"
        val expr = Expr(calc, input)
        calc.trace = Trace.ALL
        expr.parse()
        expr.calculate()
        val singleResult = calc.result()
        assertNotNull(singleResult)
        assertEquals(42.0, singleResult, 1e-9)
    }

    @Test
    fun happySingleVariableTest() {
        val input = "x"
        val expr = Expr(calc, input)
        expr.trace(Trace.ALL)
        expr[Op.X] = 90.0
        expr.parse()
        expr.calculate()
        val singleResult = calc.result()
        assertEquals(90.0, singleResult, 1e-9)
    }

    @Test
    fun happyTest() {
        val input = "sin(90)"
        val expr = Expr(calc, input)
        calc.trace = Trace.ALL
        expr.parse()
        expr.calculate()
        val singleResult = calc.result()
        assertNotNull(singleResult)
        assertEquals(1.0, singleResult, 1e-9)
        assertEquals(1, calc.stack.size, "Single result is not returned")
    }

    @Test
    fun simpleFunctionTest() {
        val input = "sin 90"
        val expr = Expr(calc, input)
        expr.trace(Trace.ALL)
        expr.parse()
        expr.calculate()
        val singleResult = expr.result()
        assertNotNull(singleResult)
        assertEquals(1.0, singleResult, 1e-9)
        assertEquals(1, calc.stack.size, "Single result is not returned")
    }

    @Test
    fun simpleFunctionsTest() {
        val input = "sin 90 + cos 0"
        val expr = Expr(calc, input)
        expr.trace(Trace.ALL)
        expr.parse()
        expr.calculate()
        val singleResult = expr.result()
        assertNotNull(singleResult)
        assertEquals(2.0, singleResult, 1e-9)
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
        assertEquals(1.0, singleResult, 1e-9)
        assertEquals(1, calc.stack.size, "Single result is not returned")
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
        logger.info("Result = ${e.message} ${e.parseError} ")
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
        calc.trace = Trace.ALL
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
        expr.calculate()
        assertEquals(2.0, calc.result())
    }

    @Test
    fun operatorsDifferentLevelTest() {
        val input = "3 + 5 * 4 + 1"
        val expr = Expr(calc, input)
        calc.trace = Trace.ALL
        expr.parse()
        expr.calculate()
        calc.result()
    }

    @Test
    fun operatorsAndFunctionTest() {
        val input = "3 + sin(9 * 10)^2 * 2 + 1"
        val expr = Expr(calc, input)
        calc.trace = Trace.ALL
        expr.parse()
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
        calc.trace = Trace.ALL
        val input = "(3 + 5 * (2 + 3) )/ 2"
        val expr = Expr(calc, input)
        expr.parse()
        expr.calculate()
        val result = calc.result()

        assertEquals(14.0, result, 1e-8)
    }

    @Test
    fun withParanthesisSimpleTest() {
        calc.trace = Trace.ALL
        val input = "(3 + 5) * 2"
        val expr = Expr(calc, input)
        expr.parse()
        expr.calculate()
        val result = calc.result()

        assertEquals(16.0, result, 1e-8)
    }

    @Test
    fun basicAndFunctionTest() {
        val input = "sin(x) + cos(x - 90)"
        val expr = Expr(calc, input)
        expr.trace(Trace.ALL)
        expr[Op.X] = 90.0
        expr[Op.Y] = 90.0
        expr.parse()
        expr.calculate()
        val result = expr.result()

        assertEquals(2.0, result, 1e-8)
    }

    @Test
    fun weirdParenthesisAndMultiVariableMonsterTest() {
        val x = 3.0
        val y = 6.0
        val z = 5.0
        calc.setAll(mapOf(Op.X to x, Op.Y to y, Op.Z to z))
        calc.trace = Trace.ALL

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
        calc.trace = Trace.ALL

        val input = "x^y"

        val expr = Expr(calc, input)
        expr.parse()
        expr.calculate()
        assertEquals(8.0, calc.result() )
    }

    @Test
    fun powerSinTest() {
        val input = "2^sin(90)"
        val expr = Expr(calc, input)
        expr.trace(Trace.ALL)
        expr.parse()
        expr.calculate()
        assertEquals(2.0, expr.result(), 1E-9 )
    }

    @Test
    fun closeMinusTest() {
        val input = "2^-2(4)"
        val expr = Expr(calc, input)
        expr.trace(Trace.ALL)
        expr.parse()
        expr.calculate()
        assertEquals(1.0, expr.result(), 1E-9 )
    }

    @Test
    fun minusMinusTest() {
        val input = "3- -3"
        val expr = Expr(calc, input)
        expr.trace(Trace.ALL)
        expr.parse()
        expr.calculate()
        assertEquals(6.0, expr.result() )
    }



    @Test
    fun powerPriorityTest() {
        val x = 2.0
        val y = 3.0
        val z = 1.0
        val input = "x^y^z"

        val expr = Expr(calc, input)
        expr.setAll(mapOf(Op.X to x, Op.Y to y, Op.Z to z))
        expr.trace(Trace.ALL)
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
        expr.trace(Trace.ALL)
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
        expr.trace(Trace.ALL)
        expr.parse()
        expr.calculate()
        assertEquals(1025.0, expr.result() )
    }

    @Test
    fun negativeNumberPowerTest() {
        val input = "-2^2"

        val expr = Expr(calc, input)
        expr.trace(Trace.ALL)
        expr.parse()
        expr.calculate()
        assertEquals(4.0, expr.result(), 1e-9 )
    }

    @Test
    fun negativeNumberPower2Test() {
        val input = "3 - -2^2"

        val expr = Expr(calc, input)
        expr.trace(Trace.ALL)
        expr.parse()
        expr.calculate()
        assertEquals(-1.0, expr.result(), 1e-9 )
    }

    @Test
    fun negativeNumberPower2FailsTest() {
        val input = "3 -2^2"

        val expr = Expr(calc, input)
        expr.trace(Trace.ALL)
        val e = assertThrows <ParseException> { expr.parse() }
        assertEquals(MISSING_OPERATOR, e.parseError.key)
    }

    @Test
    fun negativeVariableTest() {
        val input = "-x"

        val expr = Expr(calc, input)
        expr.trace(Trace.ALL)
        expr[Op.X] = 2.0
        expr.parse()
        expr.calculate()
        assertEquals(-2.0, expr.result(), 1e-9 )
    }


    //TODO Move to separate test class

    @Test
    fun setVariablesAsStringTest() {
        val calc = CalcDouble()
        val expr = Expr(calc, "x + y -z")
        expr.setVars("x=3; y=5; z=4")
        expr.parse()
        expr.calculate()
        Assertions.assertEquals(4.0, calc.result())
    }

    @Test
    fun setVariablesAsStringFailsTest() {
        val calc = CalcDouble()
        val expr = Expr(calc, "x + y -z")
        val e = assertThrows<CalcException> {
            expr.setVars("x=3; y=5;z=gg")
        }
        assertThat(e.message).startsWith("DoubleFormatError")
    }

    @Test
    fun setIllegalVariableAsStringFailsTest() {
        val calc = CalcDouble()
        val expr = Expr(calc, "x + y -z")
        val e = assertThrows<ParseException> {
            expr.setVars("x=3; y=5; zz=3")
        }
        assertEquals(VARIABLE_NOT_FOUND, e.parseError.key)
    }

    @Test
    fun multipleNegateAndSubtractTest() {
        val calc = CalcDouble()
        val expr = Expr(calc, "-x - -y")
        expr.trace(Trace.ALL)
        expr.setVars("x=10; y=4")
        expr.parse()
        expr.calculate()
        assertEquals(-6.0, calc.result())
    }

    @Test
    fun negateFunctionTest() {
        val calc = CalcDouble()
        val expr = Expr(calc, "-sin(x) + cos(y)")
        expr.trace(Trace.ALL)
        expr.setVars("x=90; y=0")
        expr.parse()
        expr.calculate()
        assertEquals(0.0, calc.result())
    }

    @Test
    fun negateParenthesisTest() {
        val expr = Expr(calc, "-(3+5)")
        expr.trace(Trace.ALL)
        expr.parse()
        expr.calculate()
        assertEquals(-8.0, calc.result())
    }

    @Test
    fun overFlowTest() {
        val expr = Expr(calc, "9^9^9")
        expr.trace(Trace.ALL)
        expr.parse()
        expr.calculate()
        assertEquals(Double.POSITIVE_INFINITY, expr.result())
    }

    @Test
    fun parenthesisHellTest() {
        val input = "((((((((((((((((((((((((((((((((((((((((((((((((((1))))))))))))))))))))))))))))))))))))))))))))))))))"
        val expr = Expr(calc, input)
        expr.trace(Trace.ALL)
        expr.parse()
        expr.calculate()
        assertEquals(1.0, expr.result())
    }

    @Test
    fun UnicodeTest() {
        val input = "a\u200B + 2"
        val expr = Expr(calc, input)
        expr.trace(Trace.ALL)
        val e = assertThrows <ParseException> { expr.parse() }
        assertThat(e.parseError.key).contains(UNPARSEABLE)
    }

    @Test
    fun RepeatedOperatorTest() {
        val input = "2^^3"
        val expr = Expr(calc, input)
        expr.trace(Trace.ALL)
        val e = assertThrows <ParseException> { expr.parse() }
        assertThat(e.parseError.key).contains(MISSING_OPERAND)
    }

    @Test
    @Disabled
    fun setIllegalVariableExpressionFailsTest() { //TODO ReierAsk Variable parser need to be improved
        val expr = Expr(calc, "x + y -z")
        val e = assertThrows<ParseException> {
            expr.setVars("snuppa")
        }
        assertEquals(VARIABLE_NOT_FOUND, e.parseError.key)
    }
}