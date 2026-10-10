package com.techreier.edrops.calc

import org.junit.jupiter.api.Test

class PreparseTest: TestBase() {

    @Test
    fun happyTest(){
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
        assertPreparseFails(UNPARSEABLE, expr)
    }

    @Test
    fun longExpressionNotFoundTest() {
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val input = "sin dette er forferdelig mange argumenter til en sinus funksjon å være, alt for mange argumenter" +
                "er det, bare tull faktisk"
        val expr = Expr(calculator, input)
        assertPreparseFails(UNPARSEABLE, expr)
    }

    @Test
    fun longExpressionTest() {
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val input = "(( ( (x ^ 2) + (y ^ 2) ) / ( (x - y) ^ 2 + 1 ) ) * ( (z) - (1) ) ) + ( ( (2.5) * x ) / ( y ) )"
        val expr = Expr(calculator, input)
        assertPreparse(tokens(Op.LEFTP,Op.LEFTP,Op.LEFTP,Op.LEFTP, Op.X,Op.POW,2.0,Op.RIGHTP,Op.ADD,Op.LEFTP)
           ,expr, 10)
    }

    @Test
    fun longExpressionFailsTest() {
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val input = "(( ( (x ^ 2) + (y ^ 2) ) / ( (a - y) ^ 2 + 1 ) ) * ( (z) - (1) ) ) + ( ( (2.5) * x ) / ( y ) )"
        val expr = Expr(calculator, input)
        assertPreparseFails(UNPARSEABLE, expr)
    }

    @Test
    fun leftParenthesisFailsTest() {
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val input = "( sin(x) + cos((y)"
        val expr = Expr(calculator, input)
        assertPreparseFails(TOO_MANY_LEFT_PARENTHESIS,expr)
    }

    @Test
    fun rightParenthesisFailsTest() {
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val input = "sin(x)) + cos(y)"
        val expr = Expr(calculator, input)
        assertPreparseFails(TOO_MANY_RIGHT_PARENTHESIS, expr)
    }

    @Test
    fun minusminusFailsTest() {
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val input = "5-- 3"
        val expr = Expr(calculator, input)
        assertPreparseFails(MISSING_OPERAND, expr)
    }

    @Test
    fun plusMinusFailsTest() {
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val input = "5+- 3"
        val expr = Expr(calculator, input)
        assertPreparseFails(MISSING_OPERAND, expr)
    }

    @Test
    fun minusTest() {
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val input = "5 - -3"
        val expr = Expr(calculator, input)
        assertPreparse(tokens(5.0, Op.SUBTRACT, -3.0), expr)
    }

    @Test
    fun doubleOperatorFailsTest() {
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val input = "3 -  - 5"
        val expr = Expr(calculator, input)
        assertPreparseFails(MISSING_OPERAND, expr)
    }

    @Test
    fun doubleFunkFailsTest() {
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val input = "sin sin(3)"
        val expr = Expr(calculator, input)
        assertPreparseFails(CONSECUTIVE_FUNCTIONS, expr)
    }

    @Test
    fun functionNumberOKTest() {
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val input = "sin 3"
        val expr = Expr(calculator, input)
        assertPreparse(tokens(Op.SIN, 3.0), expr)
    }

    @Test
    fun functionVariableOKTest() {
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val input = "sin x"
        val expr = Expr(calculator, input)
        assertPreparse(tokens(Op.SIN, Op.X), expr)
    }

    @Test
    fun varVarOKTest() {
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val input = "x y"
        val expr = Expr(calculator, input)
        assertPreparse(tokens(Op.X, Op.MULTIPLY, Op.Y), expr)
    }

    @Test
    fun varNumberFailsTest() {
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val input = "x 3"
        val expr = Expr(calculator, input)
        assertPreparseFails(MISSING_OPERATOR, expr)
    }

    @Test
    fun numberVarOKTest() {
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val input = "3 x"
        val expr = Expr(calculator, input)
        assertPreparse(tokens(3.0, Op.MULTIPLY, Op.X), expr)
    }

    @Test
    fun numberVariableExponentialOKTest() {
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

    @Test
    fun emptyParenthesisFailsTest() {
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val input = "3()"
        val expr = Expr(calculator, input)
        assertPreparseFails(EMPTY_PARENTHESIS, expr)
    }

    @Test
    fun OperatorParenthesisFailsTest() {
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val input = "3(-)"
        val expr = Expr(calculator, input)
        assertPreparseFails(MISPLACED_OPERATOR, expr)
    }

    @Test
    fun doubleParenthesisOKTest() {
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val input = "(x+5)(x-3)"
        val expr = Expr(calculator, input)
        assertPreparse(tokens(Op.LEFTP, Op.X, Op.ADD, 5.0, Op.RIGHTP, Op.MULTIPLY,
            Op.LEFTP, Op.X, Op.SUBTRACT, 3.0, Op.RIGHTP), expr)
    }

    @Test
    fun emptyFailsTest() {
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val input = ""
        val expr = Expr(calculator, input)
        assertPreparseFails(UNPARSEABLE, expr)
    }

    @Test
    fun spaceFailsTest() {
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val input = "   "
        val expr = Expr(calculator, input)
        assertPreparseFails(UNPARSEABLE, expr)
    }

    @Test
    fun justFunctionWithArgumentsTest() { // Will be checked later when parsing
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val input = "sin"
        val expr = Expr(calculator, input)
        assertPreparse(tokens(Op.SIN), expr)
    }

    @Test
    fun justMinusTest() {
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val input = "-"
        val expr = Expr(calculator, input)
        assertPreparseFails(MISPLACED_OPERATOR,expr)
    }

    @Test
    fun minusNegativeNumberTest() {
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val input = "3 --2"
        val expr = Expr(calculator, input)
        assertPreparse(tokens(3, Op.SUBTRACT, -2), expr)
    }

    @Test
    fun minusSpaceNegativeNumberTest() {
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val input = "3 - -2"
        val expr = Expr(calculator, input)
        assertPreparse(tokens(3, Op.SUBTRACT, -2), expr)
    }

    @Test
    fun minusParenthesisNegativeNumberTest() {
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val input = "3 -(-2)"
        val expr = Expr(calculator, input)
        assertPreparse(tokens(3, Op.SUBTRACT, Op.LEFTP, -2, Op.RIGHTP), expr)
    }

    @Test
    fun strangeNumberFailsTest() {
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val input = "3 * 3..2"
        val expr = Expr(calculator, input)
        assertPreparseFails(MISSING_OPERATOR, expr)
    }

    @Test
    fun strangeExponentFailsTest() {
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val input = "4-^3 +3"
        val expr = Expr(calculator, input)
        assertPreparseFails(MISSING_OPERAND, expr)
    }

    @Test
    fun constantsImplicitMultiplicationOKTest() {
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val input = "pi (3+2) 4 e"
        val expr = Expr(calculator, input)
        assertPreparse(tokens(Op.PI, Op.MULTIPLY, Op.LEFTP, 3, Op.ADD, 2,
            Op.RIGHTP, Op.MULTIPLY, 4, Op.MULTIPLY, Op.E), expr)
    }

    @Test
    fun misplacedOperatorTest() {
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val input = "+3"
        val expr = Expr(calculator, input)
        assertPreparseFails(MISPLACED_OPERATOR, expr)
    }

    @Test
    fun misplacedOperator2Test() {
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val input = "3+"
        val expr = Expr(calculator, input)
        assertPreparseFails(MISPLACED_OPERATOR, expr)
    }

    @Test
    fun misplacedRightParenthesisTest() {
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val input = ")+2"
        val expr = Expr(calculator, input)
        assertPreparseFails(MISPLACED_PARENTHESIS, expr)
    }

    @Test
    fun misplacedLeftParenthesisTest() {
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val input = "3+("
        val expr = Expr(calculator, input)
        assertPreparseFails(MISPLACED_PARENTHESIS, expr)
    }

    @Test
    fun separatorOKTest() {
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val input = "3+hyp(4;5)"
        val expr = Expr(calculator, input)
        assertPreparse(tokens(3, Op.ADD, Op.HYP, Op.LEFTP, 4, Op.SEPARATOR, 5, Op.RIGHTP ), expr)
    }

    @Test
    fun lonelySeparatorFailsTest() {
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val input = ";"
        val expr = Expr(calculator, input)
        assertPreparseFails(SEPARATOR_NOT_IN_FUNCTION , expr)
    }

    @Test
    fun separatorNotInFunctionFailsTest() {
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val input = "sin(33); cos(44)"
        val expr = Expr(calculator, input)
        assertPreparseFails(SEPARATOR_NOT_IN_FUNCTION , expr)
    }

    @Test
    fun separatorAfterLeftParenthesisFailsTest() {
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val input = "sin(;33)"
        val expr = Expr(calculator, input)
        assertPreparseFails(MISPLACED_SEPARATOR , expr)
    }

    @Test
    fun separatorBeforeRightParenthesisFailsTest() {
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val input = "sin(33;)"
        val expr = Expr(calculator, input)
        assertPreparseFails(MISPLACED_SEPARATOR , expr)
    }

    @Test
    fun doubleSeparatorFailsTest() {
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val input = "hyp(3; ;4)"
        val expr = Expr(calculator, input)
        assertPreparseFails(MISPLACED_SEPARATOR , expr)
    }

    @Test
    fun separatorAfterOperatorFailsTest() {
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val input = "hyp(+;5)"
        val expr = Expr(calculator, input)
        assertPreparseFails(MISPLACED_SEPARATOR , expr)
    }

    @Test
    fun separatorBeforerOperatorFailsTest() {
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val input = "hyp(3;- 5)"
        val expr = Expr(calculator, input)
        assertPreparseFails(MISSING_OPERAND , expr)
    }

    @Test
    fun separatorBeforeMinusTest() {
        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val input = "hyp(x; -5)"
        val expr = Expr(calculator, input)
        assertPreparse(tokens(Op.HYP, Op.LEFTP, Op.X, Op.SEPARATOR, -5, Op.RIGHTP) , expr)
    }

}

