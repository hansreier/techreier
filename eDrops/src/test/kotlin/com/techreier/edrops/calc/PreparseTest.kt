package com.techreier.edrops.calc

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class PreparseTest {

    @Test
    fun happyTest() {
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val input = "3.0(x)"
        val expr = Expr(calculator, input)
        val parsed = expr.preparse()
        expr.logParseErrors()
        assertTrue(parsed)
        assertThat(expr.xTokens).size().isEqualTo(4)
        assertEquals(TokenType.NUMBER,expr.xTokens[0].type)
        assertEquals(3.0,expr.xTokens[0].argument)
        assertEquals(TokenType.OPERATOR,expr.xTokens[1].type)
        assertEquals(Op.LEFTP,expr.xTokens[1].operator)
        assertEquals(TokenType.OPERATOR,expr.xTokens[2].type)
        assertEquals(Op.X,expr.xTokens[2].operator)
        assertEquals(TokenType.OPERATOR,expr.xTokens[3].type)
        assertEquals(Op.RIGHTP,expr.xTokens[3].operator)
    }

    @Test
    fun expressionNotFoundTest() {
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val input = "Bullshit"
        val expr = Expr(calculator, input)
        val preparsed = expr.preparse()
        expr.logParseErrors()
        assertFalse(preparsed)
    }
    @Test
    fun longExpressionNotFoundTest() {
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val input = "sin dette er forferdelig mange argumenter til en sinus funksjon å være, alt for mange argumenter" +
                "er det, bare tull faktisk"
        val expr = Expr(calculator, input)
        val preparsed = expr.preparse()
        expr.logParseErrors()
        assertFalse(preparsed)
    }

    @Test
    fun longExpressionnTest() {
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val input = "(( ( (x ^ 2) + (y ^ 2) ) / ( (x - y) ^ 2 + 1 ) ) * ( (z) - (1) ) ) + ( ( (2.5) * x ) / ( y ) )"
        val expr = Expr(calculator, input)
        val preparsed = expr.preparse()
        expr.logParseErrors()
        assertTrue(preparsed)
    }

    @Test
    fun longExpressionFailedTest() {
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val input = "(( ( (x ^ 2) + (y ^ 2) ) / ( (a - y) ^ 2 + 1 ) ) * ( (z) - (1) ) ) + ( ( (2.5) * x ) / ( y ) )"
        val expr = Expr(calculator, input)
        val preparsed = expr.preparse()
        expr.logParseErrors()
        assertFalse(preparsed)
    }

    @Test
    fun leftParenthesisFailedTest() {
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val input = "( sin(x) + cos((y)"
        val expr = Expr(calculator, input)
        val preparsed = expr.preparse()
        expr.logParseErrors()
        assertFalse(preparsed)
    }

    @Test
    fun rightParenthesisFailedTest() {
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val input = "sin(x)) + cos(y)"
        val expr = Expr(calculator, input)
        val preparsed = expr.preparse()
        expr.logParseErrors()
        assertFalse(preparsed)
    }

    @Test
    fun minusminusFailedTest() {
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val input = "5-- 3"
        val expr = Expr(calculator, input)
        val preparsed = expr.preparse()
        expr.logParseErrors()
        assertFalse(preparsed)
    }

    @Test
    fun minusplusFailedTest() {
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val input = "5+- 3"
        val expr = Expr(calculator, input)
        val preparsed = expr.preparse()
        expr.logParseErrors()
        assertFalse(preparsed)
    }

    @Test
    fun minusTest() {
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val input = "5 - -3"
        val expr = Expr(calculator, input)
        val preparsed = expr.preparse()
        expr.logParseErrors()
        assertTrue(preparsed)
    }

    @Test
    fun sinsinFailsTest() {
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val input = "sin sin(3)"
        val expr = Expr(calculator, input)
        val preparsed = expr.preparse()
        expr.logParseErrors()
        assertFalse(preparsed)
    }

    @Test
    fun sinOKTest() { //TODO ReierAsk evaluate if allow this
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val input = "sin 3"
        val expr = Expr(calculator, input)
        val preparsed = expr.preparse()
        expr.logParseErrors()
        assertTrue(preparsed)
    }

    @Test
    fun sinVarOKTest() {
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val input = "sin x"
        val expr = Expr(calculator, input)
        val preparsed = expr.preparse()
        expr.logParseErrors()
        assertTrue(preparsed)
    }

    @Test
    fun varVarFails() {
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val input = "x y"
        val expr = Expr(calculator, input)
        val preparsed = expr.preparse()
        expr.logParseErrors()
        assertFalse(preparsed)
    }

    @Test
    fun varNumberFails() {
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val input = "x 3"
        val expr = Expr(calculator, input)
        val preparsed = expr.preparse()
        expr.logParseErrors()
        assertFalse(preparsed)
    }

}

