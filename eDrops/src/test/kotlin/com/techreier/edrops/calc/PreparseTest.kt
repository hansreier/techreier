package com.techreier.edrops.calc

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class PreparseTest {

    @Test
    fun happyTest() {
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val input = "3.0(x)+y"
        val expr = Expr(calculator, input)
        assertPreparse(tokens(3.0, Op.MULTIPLY, Op.LEFTP, Op.X, Op.RIGHTP, Op.ADD, Op.Y), expr)
    }

    @Test
    fun expressionNotFoundTest() {
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val input = "Bullshit"
        val expr = Expr(calculator, input)
        assertPreparseFails(expr)
    }

    @Test
    fun longExpressionNotFoundTest() {
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val input = "sin dette er forferdelig mange argumenter til en sinus funksjon å være, alt for mange argumenter" +
                "er det, bare tull faktisk"
        val expr = Expr(calculator, input)
        assertPreparseFails(expr)
    }

    @Test
    fun longExpressionTest() {
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val input = "(( ( (x ^ 2) + (y ^ 2) ) / ( (x - y) ^ 2 + 1 ) ) * ( (z) - (1) ) ) + ( ( (2.5) * x ) / ( y ) )"
        val expr = Expr(calculator, input)
       assertPreparse(tokens(Op.LEFTP,Op.LEFTP,Op.LEFTP,Op.LEFTP, Op.X,Op.POW,2.0,Op.RIGHTP,Op.ADD,Op.LEFTP)
           ,expr, false)
    }

    @Test
    fun longExpressionFailsTest() {
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val input = "(( ( (x ^ 2) + (y ^ 2) ) / ( (a - y) ^ 2 + 1 ) ) * ( (z) - (1) ) ) + ( ( (2.5) * x ) / ( y ) )"
        val expr = Expr(calculator, input)
        assertPreparseFails(expr)
    }

    @Test
    fun leftParenthesisFailsTest() {
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val input = "( sin(x) + cos((y)"
        val expr = Expr(calculator, input)
        assertPreparseFails(expr)
    }

    @Test
    fun rightParenthesisFailsTest() {
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val input = "sin(x)) + cos(y)"
        val expr = Expr(calculator, input)
        assertPreparseFails(expr)
    }

    @Test
    fun minusminusFailsTest() {
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val input = "5-- 3"
        val expr = Expr(calculator, input)
        assertPreparseFails(expr)
    }

    @Test
    fun minusplusFailsTest() {
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val input = "5+- 3"
        val expr = Expr(calculator, input)
        assertPreparseFails(expr)
    }

    @Test
    fun minusTest() {
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val input = "5 - -3"
        val expr = Expr(calculator, input)
        assertPreparse(tokens(5.0, Op.SUBTRACT, -3.0), expr)
    }

    @Test
    fun sinsinFailsTest() {
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val input = "sin sin(3)"
        val expr = Expr(calculator, input)
        assertPreparseFails(expr)
    }

    @Test
    fun sinOKTest() {
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val input = "sin 3"
        val expr = Expr(calculator, input)
        assertPreparse(tokens(Op.SIN, 3.0), expr)
    }

    @Test
    fun sinVarOKTest() {
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val input = "sin x"
        val expr = Expr(calculator, input)
        assertPreparse(tokens(Op.SIN, Op.X), expr)
    }

    @Test
    fun varVarFailsTest() {
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val input = "x y"
        val expr = Expr(calculator, input)
        assertPreparseFails(expr)
    }

    @Test
    fun varNumberFailsTest() {
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val input = "x 3"
        val expr = Expr(calculator, input)
        assertPreparseFails(expr)
    }

    @Test
    fun numberVarOKTest() {
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val input = "3 x"
        val expr = Expr(calculator, input)
        assertPreparse(tokens(3.0, Op.MULTIPLY, Op.X), expr)
    }

    @Test
    fun numberVariableExponentialTest() {
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val input = "3x^4"
        val expr = Expr(calculator, input)
        assertPreparse(tokens(3.0, Op.MULTIPLY, Op.X,Op.POW, 4.0), expr)
    }

    @Test
    fun numberLeftParenthesisOKTest() {
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val input = "3(x+5)"
        val expr = Expr(calculator, input)
        assertPreparse(tokens(3.0, Op.MULTIPLY, Op.LEFTP, Op.X, Op.ADD, 5.0, Op.RIGHTP), expr)
    }

    //TODO ReierAsk fails x-3, Var Number not allowed wrong in this case
    @Test
    fun doubleParenthesisOKTest() {
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val input = "(x+5)(x-3)"
        val expr = Expr(calculator, input)
        assertPreparse(tokens(Op.LEFTP, Op.X, Op.ADD, 5.0, Op.RIGHTP, 3.0, Op.MULTIPLY,
            Op.LEFTP, Op.X, Op.SUBTRACT, 3.0, Op.RIGHTP), expr)
    }

    private fun assertPreparseFails(expr: Expr) {
        assertFalse(expr.preparse(), expr.expr)
    }

    private fun assertPreparse(expectedTokens: ArrayList<Token>, expr: Expr, strict: Boolean = true) {
        expr.preparse()
        val actualTokens = expr.xTokens
        expr.parseErrorMessage()
        assertFalse(expr.parseErrors.isNotEmpty(), "parseError:\n${expr.parseErrorMessage()}")

        if (strict) {
            assertTrue(
                actualTokens.size == expectedTokens.size,
                "Wrong number of tokens (${actualTokens.size}),should be ${expectedTokens.size}"
            )
        } else {
            assertTrue(
                actualTokens.size >= expectedTokens.size,
                "Too few tokens (${actualTokens.size}),should be at least ${expectedTokens.size}"
            )
        }

        expectedTokens.forEachIndexed { index, expected ->
            val actual = actualTokens[index]
            assertEquals(expected.type, actual.type, "tokentype differs")
            if (expected.type == TokenType.OPERATOR)
                assertEquals(expected.operator, actual.operator, "operator differs  \n " +
                        expr.errorIndicator(actual.position)
                )
            else
                assertEquals(expected.argument, actual.argument, "argument differs \n " +
                        expr.errorIndicator(actual.position)
                )
        }
    }

    private fun tokens(vararg items: Any): ArrayList<Token> {
        return arrayListOf(*items.map { item ->
            when (item) {
                is Number -> Token(item.toDouble())
                is Op -> Token(item)
                else -> throw IllegalArgumentException("Unknown token-type expected: $item")
            }
        }.toTypedArray())
    }

}

