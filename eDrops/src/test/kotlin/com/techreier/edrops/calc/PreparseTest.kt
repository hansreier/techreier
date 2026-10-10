package com.techreier.edrops.calc

import org.junit.jupiter.api.Test

class PreparseTest : TestBase() {


    @Test
    fun happyTest() {
        val calc: Calc<Double> = CalcDouble(Double::class.javaObjectType)
        val input = "3.0(x)+y"
        assertPreparse(tokens(3.0, Op.MULTIPLY, Op.LEFTP, Op.X, Op.RIGHTP, Op.ADD, Op.Y), calc, input)
    }

    @Test
    fun expressionNotFoundTest() {
        val calc: Calc<Double> = CalcDouble(Double::class.javaObjectType)
        val input = "Bullshit"
        assertPreparseFails(UNPARSEABLE, calc, input)
    }

    @Test
    fun longExpressionNotFoundTest() {
        val calc: Calc<Double> = CalcDouble(Double::class.javaObjectType)
        val input = "sin dette er forferdelig mange argumenter til en sinus funksjon å være, alt for mange argumenter" +
                "er det, bare tull faktisk"
        assertPreparseFails(UNPARSEABLE, calc, input)
    }

    @Test
    fun longExpressionTest() {
        val calc: Calc<Double> = CalcDouble(Double::class.javaObjectType)
        val input = "(( ( (x ^ 2) + (y ^ 2) ) / ( (x - y) ^ 2 + 1 ) ) * ( (z) - (1) ) ) + ( ( (2.5) * x ) / ( y ) )"
        assertPreparse(
            tokens(Op.LEFTP, Op.LEFTP, Op.LEFTP, Op.LEFTP, Op.X, Op.POW, 2.0, Op.RIGHTP, Op.ADD, Op.LEFTP),
            calc, input, 10
        )
    }

    @Test
    fun longExpressionFailsTest() {
        val calc: Calc<Double> = CalcDouble(Double::class.javaObjectType)
        val input = "(( ( (x ^ 2) + (y ^ 2) ) / ( (a - y) ^ 2 + 1 ) ) * ( (z) - (1) ) ) + ( ( (2.5) * x ) / ( y ) )"
        assertPreparseFails(UNPARSEABLE, calc, input)
    }

    @Test
    fun leftParenthesisFailsTest() {
        val calc: Calc<Double> = CalcDouble(Double::class.javaObjectType)
        val input = "( sin(x) + cos((y)"
        assertPreparseFails(TOO_MANY_LEFT_PARENTHESIS, calc, input)
    }

    @Test
    fun rightParenthesisFailsTest() {
        val calc: Calc<Double> = CalcDouble(Double::class.javaObjectType)
        val input = "sin(x)) + cos(y)"
        assertPreparseFails(TOO_MANY_RIGHT_PARENTHESIS, calc, input)
    }

    @Test
    fun minusminusFailsTest() {
        val calc: Calc<Double> = CalcDouble(Double::class.javaObjectType)
        val input = "5-- 3"
        assertPreparseFails(MISSING_OPERAND, calc, input)
    }

    @Test
    fun plusMinusFailsTest() {
        val calc: Calc<Double> = CalcDouble(Double::class.javaObjectType)
        val input = "5+- 3"
        assertPreparseFails(MISSING_OPERAND, calc, input)
    }

    @Test
    fun minusTest() {
        val calc: Calc<Double> = CalcDouble(Double::class.javaObjectType)
        val input = "5 - -3"
        assertPreparse(tokens(5.0, Op.SUBTRACT, -3.0), calc, input)
    }

    @Test
    fun doubleOperatorFailsTest() {
        val calc: Calc<Double> = CalcDouble(Double::class.javaObjectType)
        val input = "3 -  - 5"
        assertPreparseFails(MISSING_OPERAND, calc, input)
    }

    @Test
    fun doubleFunkFailsTest() {
        val calc: Calc<Double> = CalcDouble(Double::class.javaObjectType)
        val input = "sin sin(3)"
        assertPreparseFails(CONSECUTIVE_FUNCTIONS, calc, input)
    }

    @Test
    fun functionNumberOKTest() {
        val calc: Calc<Double> = CalcDouble(Double::class.javaObjectType)
        val input = "sin 3"
        assertPreparse(tokens(Op.SIN, 3.0), calc, input)
    }

    @Test
    fun functionVariableOKTest() {
        val calc: Calc<Double> = CalcDouble(Double::class.javaObjectType)
        val input = "sin x"
        assertPreparse(tokens(Op.SIN, Op.X), calc, input)
    }

    @Test
    fun varVarOKTest() {
        val calc: Calc<Double> = CalcDouble(Double::class.javaObjectType)
        val input = "x y"
        assertPreparse(tokens(Op.X, Op.MULTIPLY, Op.Y), calc, input)
    }

    @Test
    fun varNumberFailsTest() {
        val calc: Calc<Double> = CalcDouble(Double::class.javaObjectType)
        val input = "x 3"
        assertPreparseFails(MISSING_OPERATOR, calc, input)
    }

    @Test
    fun numberVarOKTest() {
        val calc: Calc<Double> = CalcDouble(Double::class.javaObjectType)
        val input = "3 x"

        assertPreparse(tokens(3.0, Op.MULTIPLY, Op.X), calc, input)
    }

    @Test
    fun numberVariableExponentialOKTest() {
        val calc: Calc<Double> = CalcDouble(Double::class.javaObjectType)
        val input = "3x^4"
        assertPreparse(tokens(3.0, Op.MULTIPLY, Op.X, Op.POW, 4.0), calc, input)
    }

    @Test
    fun numberLeftParenthesisOKTest() {
        val calc: Calc<Double> = CalcDouble(Double::class.javaObjectType)
        val input = "3(x+5)"
        assertPreparse(tokens(3.0, Op.MULTIPLY, Op.LEFTP, Op.X, Op.ADD, 5.0, Op.RIGHTP), calc, input)
    }

    @Test
    fun emptyParenthesisFailsTest() {
        val calc: Calc<Double> = CalcDouble(Double::class.javaObjectType)
        val input = "3()"
        assertPreparseFails(EMPTY_PARENTHESIS, calc, input)
    }

    @Test
    fun OperatorParenthesisFailsTest() {
        val calc: Calc<Double> = CalcDouble(Double::class.javaObjectType)
        val input = "3(-)"
        assertPreparseFails(MISPLACED_OPERATOR, calc, input)
    }

    @Test
    fun doubleParenthesisOKTest() {
        val calc: Calc<Double> = CalcDouble(Double::class.javaObjectType)
        val input = "(x+5)(x-3)"
        assertPreparse(
            tokens(
                Op.LEFTP, Op.X, Op.ADD, 5.0, Op.RIGHTP, Op.MULTIPLY,
                Op.LEFTP, Op.X, Op.SUBTRACT, 3.0, Op.RIGHTP
            ), calc, input
        )
    }

    @Test
    fun emptyFailsTest() {
        val calc: Calc<Double> = CalcDouble(Double::class.javaObjectType)
        val input = ""
        assertPreparseFails(UNPARSEABLE, calc, input)
    }

    @Test
    fun spaceFailsTest() {
        val calc: Calc<Double> = CalcDouble(Double::class.javaObjectType)
        val input = "   "
        assertPreparseFails(UNPARSEABLE, calc, input)
    }

    @Test
    fun justFunctionWithArgumentsTest() { // Will be checked later when parsing
        val calc: Calc<Double> = CalcDouble(Double::class.javaObjectType)
        val input = "sin"
        assertPreparse(tokens(Op.SIN), calc, input)
    }

    @Test
    fun justMinusTest() {
        val calc: Calc<Double> = CalcDouble(Double::class.javaObjectType)
        val input = "-"
        assertPreparseFails(MISPLACED_OPERATOR, calc, input)
    }

    @Test
    fun minusNegativeNumberTest() {
        val calc: Calc<Double> = CalcDouble(Double::class.javaObjectType)
        val input = "3 --2"
        assertPreparse(tokens(3, Op.SUBTRACT, -2), calc, input)
    }

    @Test
    fun minusSpaceNegativeNumberTest() {
        val calc: Calc<Double> = CalcDouble(Double::class.javaObjectType)
        val input = "3 - -2"
        assertPreparse(tokens(3, Op.SUBTRACT, -2), calc, input)
    }

    @Test
    fun minusParenthesisNegativeNumberTest() {
        val calc: Calc<Double> = CalcDouble(Double::class.javaObjectType)
        val input = "3 -(-2)"
        assertPreparse(tokens(3, Op.SUBTRACT, Op.LEFTP, -2, Op.RIGHTP), calc, input)
    }

    @Test
    fun strangeNumberFailsTest() {
        val calc: Calc<Double> = CalcDouble(Double::class.javaObjectType)
        val input = "3 * 3..2"
        assertPreparseFails(MISSING_OPERATOR, calc, input)
    }

    @Test
    fun strangeExponentFailsTest() {
        val calc: Calc<Double> = CalcDouble(Double::class.javaObjectType)
        val input = "4-^3 +3"
        assertPreparseFails(MISSING_OPERAND, calc, input)
    }

    @Test
    fun constantsImplicitMultiplicationOKTest() {
        val calc: Calc<Double> = CalcDouble(Double::class.javaObjectType)
        val input = "pi (3+2) 4 e"
        assertPreparse(
            tokens(
                Op.PI, Op.MULTIPLY, Op.LEFTP, 3, Op.ADD, 2,
                Op.RIGHTP, Op.MULTIPLY, 4, Op.MULTIPLY, Op.E
            ), calc, input
        )
    }

    @Test
    fun misplacedOperatorTest() {
        val calc: Calc<Double> = CalcDouble(Double::class.javaObjectType)
        val input = "+3"
        assertPreparseFails(MISPLACED_OPERATOR, calc, input)
    }

    @Test
    fun misplacedOperator2Test() {
        val calc: Calc<Double> = CalcDouble(Double::class.javaObjectType)
        val input = "3+"
        assertPreparseFails(MISPLACED_OPERATOR, calc, input)
    }

    @Test
    fun misplacedRightParenthesisTest() {
        val calc: Calc<Double> = CalcDouble(Double::class.javaObjectType)
        val input = ")+2"
        assertPreparseFails(MISPLACED_PARENTHESIS, calc, input)
    }

    @Test
    fun misplacedLeftParenthesisTest() {
        val calc: Calc<Double> = CalcDouble(Double::class.javaObjectType)
        val input = "3+("
        assertPreparseFails(MISPLACED_PARENTHESIS, calc, input)
    }

    @Test
    fun separatorOKTest() {
        val calc: Calc<Double> = CalcDouble(Double::class.javaObjectType)
        val input = "3+hyp(4;5)"
        assertPreparse(tokens(
            3, Op.ADD, Op.HYP, Op.LEFTP, 4, Op.SEPARATOR, 5, Op.RIGHTP
            ), calc, input
        )
    }

    @Test
    fun lonelySeparatorFailsTest() {
        val calc: Calc<Double> = CalcDouble(Double::class.javaObjectType)
        val input = ";"
        assertPreparseFails(SEPARATOR_NOT_IN_FUNCTION, calc, input)
    }

    @Test
    fun separatorNotInFunctionFailsTest() {
        val calc: Calc<Double> = CalcDouble(Double::class.javaObjectType)
        val input = "sin(33); cos(44)"
        assertPreparseFails(SEPARATOR_NOT_IN_FUNCTION, calc, input)
    }

    @Test
    fun separatorAfterLeftParenthesisFailsTest() {
        val calc: Calc<Double> = CalcDouble(Double::class.javaObjectType)
        val input = "sin(;33)"
        assertPreparseFails(MISPLACED_SEPARATOR, calc, input)
    }

    @Test
    fun separatorBeforeRightParenthesisFailsTest() {
        val calc: Calc<Double> = CalcDouble(Double::class.javaObjectType)
        val input = "sin(33;)"

        assertPreparseFails(MISPLACED_SEPARATOR, calc, input)
    }

    @Test
    fun doubleSeparatorFailsTest() {
        val calc: Calc<Double> = CalcDouble(Double::class.javaObjectType)
        val input = "hyp(3; ;4)"
        assertPreparseFails(MISPLACED_SEPARATOR, calc, input)
    }

    @Test
    fun separatorAfterOperatorFailsTest() {
        val calc: Calc<Double> = CalcDouble(Double::class.javaObjectType)
        val input = "hyp(+;5)"
        assertPreparseFails(MISPLACED_SEPARATOR, calc, input)
    }

    @Test
    fun separatorBeforerOperatorFailsTest() {
        val calc: Calc<Double> = CalcDouble(Double::class.javaObjectType)
        val input = "hyp(3;- 5)"
        assertPreparseFails(MISSING_OPERAND, calc, input)
    }

    @Test
    fun separatorBeforeMinusTest() {
        val calc: Calc<Double> = CalcDouble(Double::class.javaObjectType)
        val input = "hyp(x; -5)"
        assertPreparse(tokens(Op.HYP, Op.LEFTP, Op.X, Op.SEPARATOR, -5, Op.RIGHTP), calc, input)
    }

}

