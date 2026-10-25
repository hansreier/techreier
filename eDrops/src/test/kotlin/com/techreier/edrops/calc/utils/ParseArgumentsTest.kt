package com.techreier.edrops.calc.utils

import com.techreier.edrops.calc.NumberToken
import com.techreier.edrops.calc.Op
import com.techreier.edrops.calc.OperatorToken
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ParseArgumentsTest {

    @Test
    fun oneArgumentNoParenthesisTest() {
        val tokens = listOf(
            OperatorToken(Op.SIN, 0),
            NumberToken(90, 1),
        )
        val range = Range(0, 1)
        val argumentInfo = parseArguments(range, tokens)
        assertThat(argumentInfo.tokens).hasSize(1)
        assertThat(argumentInfo.range).isEqualTo(Range(1, 1))
        assertThat(argumentInfo.nextPos).isEqualTo(2)
        assertThat(argumentInfo.push).isTrue()
        assertThat(argumentInfo.tokens[0]).isEqualTo(tokens[1])
    }

    @Test
    fun oneArgumentParenthesisTest() {
        val tokens = listOf(
            OperatorToken(Op.SIN, 0),
            OperatorToken(Op.LEFTP, 1),
            OperatorToken(Op.LEFTP, 2),
            NumberToken(90, 3),
            OperatorToken(Op.RIGHTP, 4),
            OperatorToken(Op.RIGHTP, 5),
        )
        val range = Range(0, 1)
        val argumentInfo = parseArguments(range, tokens)
        assertThat(argumentInfo.tokens).hasSize(1)
        assertThat(argumentInfo.range).isEqualTo(Range(3, 3))
        assertThat(argumentInfo.nextPos).isEqualTo(4)
        assertThat(argumentInfo.push).isTrue()
        assertThat(argumentInfo.tokens[0]).isEqualTo(tokens[2])
    }
}