package com.techreier.edrops.calc.utils

import com.techreier.edrops.calc.NumberToken
import com.techreier.edrops.calc.Op
import com.techreier.edrops.calc.OperatorToken
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull

class CalcUtilTest {

    @Test
    fun parseDoubleTest() {
        val double = parseDouble("3.14")
        assertNotNull(double)
        Assertions.assertThat(double).isInstanceOf(Double::class.javaObjectType)
        Assertions.assertThat(double).isEqualTo(3.14)
    }

    @Test
    fun parseMinusTest() {
        val double = parseDouble("-3.14")
        assertNotNull(double)
        Assertions.assertThat(double).isInstanceOf(Double::class.javaObjectType)
        Assertions.assertThat(double).isEqualTo(- 3.14)
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
        val double = parseDouble("3,14")
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
    fun outerNoPTest() {
        val tokens = listOf(NumberToken(3, 1), OperatorToken(Op.ADD, 2), NumberToken(4, 3))
        val preRange = Range(0, 2)
        val expectedRange = Range(0, 2)
        val range = removeOuterP(preRange, tokens)
        org.junit.jupiter.api.Assertions.assertEquals(expectedRange, range)
    }
}