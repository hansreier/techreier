package com.techreier.edrops.calc.utils

import com.techreier.edrops.calc.NumberToken
import com.techreier.edrops.calc.Op
import com.techreier.edrops.calc.OperatorToken
import com.techreier.edrops.util.Trace
import com.techreier.edrops.util.TraceLevel
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull

class CalcUtilTest {

    @BeforeEach
    fun setTrace() {
        Trace.level = TraceLevel.INFO
        Trace.debug("")
    }

    @Test
    fun parseDoubleTest() {
        val double = parseDouble("3.14")
        assertNotNull(double)
        assertThat(double).isInstanceOf(Double::class.javaObjectType)
        assertThat(double).isEqualTo(3.14)
    }

    @Test
    fun parseDoubleExponentAddTest() {
        val double = parseDouble("1E-5+3")
        assertNotNull(double)
        assertThat(double).isInstanceOf(Double::class.javaObjectType)
        assertThat(double).isEqualTo(1.0E-5)
    }

    @Test
    fun parseDoubleExponentFuncTest3() {
        val double = parseDouble("1E-5abs")
        assertNotNull(double)
        assertThat(double).isInstanceOf(Double::class.javaObjectType)
        assertThat(double).isEqualTo(1.0E-5)
    }

    @Test
    fun parseDoubleExponentSubtractTest2() {
        val double = parseDouble("1E-5-3")
        assertNotNull(double)
        assertThat(double).isInstanceOf(Double::class.javaObjectType)
        assertThat(double).isEqualTo(1.0E-5)
    }

    @Test
    fun parseDoubleLowercaseExponentTest() {
        val double = parseDouble("2.0e+3")
        assertNotNull(double)
        assertThat(double).isInstanceOf(Double::class.javaObjectType)
        assertThat(double).isEqualTo(2.0E3)
    }

    @Test
    fun parseDoubleLowercaseExponentNoPlusTest() {
        val double = parseDouble("2.0e3")
        assertNotNull(double)
        assertThat(double).isInstanceOf(Double::class.javaObjectType)
        assertThat(double).isEqualTo(2.0E3)
    }

    @Test
    fun parseDoubleExponentCommaTest() {
        val double = parseDouble("2.0E,3")
        assertNotNull(double)
        assertThat(double).isInstanceOf(Double::class.javaObjectType)
        assertThat(double).isEqualTo(2.0)
    }

    @Test
    fun parseDoubleExponentDotTest() {
        val double = parseDouble("2.0E0.3")
        assertNotNull(double)
        assertThat(double).isInstanceOf(Double::class.javaObjectType)
        assertThat(double).isEqualTo(2.0)
    }

    @Test
    fun parseDoubleWrongExponentTest() {  //e+ is interpreted as the rest of the expression
        val double = parseDouble("2.0e+")
        assertNotNull(double)
        assertThat(double).isInstanceOf(Double::class.javaObjectType)
        assertThat(double).isEqualTo(2.0)
    }

    @Test
    fun parseDoubleOnlyETest() { //e is interpreted as the rest of the expression
        val double = parseDouble("2.0e-x")
        assertNotNull(double)
        assertThat(double).isInstanceOf(Double::class.javaObjectType)
        assertThat(double).isEqualTo(2.0)
    }

    @Test
    fun parseSeparateMinusTest() {
        val double = parseDouble("- 3.14")
        org.junit.jupiter.api.Assertions.assertNull(double)
    }

    @Test
    fun parseNothingTest() {
        val double = parseDouble("")
        org.junit.jupiter.api.Assertions.assertNull(double)
    }

    @Test
    fun parseWhiteSpaceTest() {
        val double = parseDouble("  ")
        org.junit.jupiter.api.Assertions.assertNull(double)
    }

    @Test
    fun parseWhiteSpaceNumberTest() {
        val double = parseDouble(" -3")
        org.junit.jupiter.api.Assertions.assertNull(double)
    }


    @Test
    fun parseDoubleNorwegianTest() {
        val double = parseDouble("3,14e-")
        assertNotNull(double)
        Assertions.assertThat(double).isInstanceOf(Double::class.javaObjectType)
        Assertions.assertThat(double).isEqualTo(3.14)
    }

    @Test
    fun parseDoubleIntegerTest() {
        val double = parseDouble("3")
        assertNotNull(double)
        Assertions.assertThat(double).isInstanceOf(Double::class.javaObjectType)
        Assertions.assertThat(double).isEqualTo(3.0)
    }

    @Test
    fun parseDoubleDotTest() {
        val double = parseDouble("3.")
        assertNotNull(double)
        Assertions.assertThat(double).isInstanceOf(Double::class.javaObjectType)
        Assertions.assertThat(double).isEqualTo(3.0)
    }

    @Test
    fun parseDoubleCommaTest() {
        val double = parseDouble("3,")
        assertNotNull(double)
        Assertions.assertThat(double).isInstanceOf(Double::class.javaObjectType)
        Assertions.assertThat(double).isEqualTo(3.0)
    }

    @Test
    fun outerNoPTest() {
        val tokens = listOf(NumberToken(3, 1), OperatorToken(Op.ADD, 2), NumberToken(4, 3))
        val preRange = Range(0, 2)
        val expectedRange = Range(0, 2)
        val range = removeOuterP(preRange, tokens)
        org.junit.jupiter.api.Assertions.assertEquals(expectedRange, range)
    }

    @Test
    fun outerNoInnerContents() {
        val tokens = listOf(
            OperatorToken(Op.LEFTP, 1), OperatorToken(Op.LEFTP, 2),
            OperatorToken(Op.RIGHTP, 3), OperatorToken(Op.RIGHTP, 4)
        )
        val preRange = Range(0, 3)
        val expectedRange = Range(2, 1)
        val range = removeOuterP(preRange, tokens)
        org.junit.jupiter.api.Assertions.assertEquals(expectedRange, range)
    }

    @Test
    fun outerContents() {
        val tokens = listOf(
            OperatorToken(Op.LEFTP, 1), OperatorToken(Op.LEFTP, 2),
            NumberToken(33.0, 3), OperatorToken(Op.RIGHTP, 4), OperatorToken(Op.RIGHTP, 5)
        )
        val preRange = Range(0, 4)
        val expectedRange = Range(2, 2)
        val range = removeOuterP(preRange, tokens)
        org.junit.jupiter.api.Assertions.assertEquals(expectedRange, range)
    }
}