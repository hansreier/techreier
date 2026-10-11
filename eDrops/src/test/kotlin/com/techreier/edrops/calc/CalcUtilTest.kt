package com.techreier.edrops.calc

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class CalcUtilTest {

    // (((3 + 4) * (5 - 1) + (6 / (2 + 1))))
    private val tokens = listOf(
        OperatorToken(Op.LEFTP, 0),
        OperatorToken(Op.LEFTP, 1),
        OperatorToken(Op.LEFTP, 2),
        NumberToken(3, 3),
        OperatorToken(Op.ADD, 4),
        NumberToken(4, 5),
        OperatorToken(Op.RIGHTP, 6),  // End (3 + 4)
        OperatorToken(Op.MULTIPLY, 7),
        OperatorToken(Op.LEFTP, 8),
        NumberToken(5, 9),
        OperatorToken(Op.SUBTRACT, 10),
        NumberToken(1, 11),
        OperatorToken(Op.RIGHTP, 12), // End (5 - 1)
        OperatorToken(Op.ADD, 13),
        OperatorToken(Op.LEFTP, 14),
        NumberToken(6, 15),
        OperatorToken(Op.DIVIDE, 16),
        OperatorToken(Op.LEFTP, 17),
        NumberToken(2, 18),
        OperatorToken(Op.ADD, 19),
        NumberToken(1, 20),
        OperatorToken(Op.RIGHTP, 21),
        OperatorToken(Op.RIGHTP, 22),
        OperatorToken(Op.RIGHTP, 23),
        OperatorToken(Op.RIGHTP, 24)
    )

    @Test
    fun parseDoubleTest() {
        val double = parseDouble("3.14")
        assertNotNull(double)
        assertThat(double).isInstanceOf(Double::class.javaObjectType)
        assertThat(double).isEqualTo(3.14)
    }

    @Test
    fun parseDoubleNorwegianTest() {
        val double = parseDouble("3,14")
        assertNotNull(double)
        assertThat(double).isInstanceOf(Double::class.javaObjectType)
        assertThat(double).isEqualTo(3.14)
    }

    @Test
    fun parseDoubleIntegerTest() {
        val double = parseDouble("3")
        assertNotNull(double)
        assertThat(double).isInstanceOf(Double::class.javaObjectType)
        assertThat(double).isEqualTo(3.0)
    }

    @Test
    fun removeOuterPTest() {
        val preRange = Range(0, tokens.size - 1)
        val expectedRange = Range(2, tokens.size -3)
        val range = removeOuterP(preRange, tokens)
        assertEquals(expectedRange, range)
    }

}