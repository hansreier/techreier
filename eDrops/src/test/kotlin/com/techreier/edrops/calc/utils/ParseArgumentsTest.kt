package com.techreier.edrops.calc.utils

import com.techreier.edrops.calc.NumberToken
import com.techreier.edrops.calc.Op
import com.techreier.edrops.calc.OperatorToken
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ParseArgumentsTest {


    @Test
    fun oneArgumentTest() {
        val tokens = listOf(
            OperatorToken(Op.SIN, 0),
            OperatorToken(Op.LEFTP, 1),
            OperatorToken(Op.LEFTP, 2),
            NumberToken(90, 3),
            OperatorToken(Op.RIGHTP, 4),
            OperatorToken(Op.RIGHTP, 5),
        )
        val range = Range(0, 5)
        val argumentInfo = parseArguments(range, tokens)
        assertThat(argumentInfo.tokens).hasSize(1)
        assertThat(argumentInfo.range).isEqualTo(Range(2, 4))  //TODO ReierAsk works but incorrect
        assertThat(argumentInfo.nextPos).isEqualTo(6)
        assertThat(argumentInfo.push).isTrue()
        assertThat(argumentInfo.tokens[0]).isEqualTo(tokens[3])
    }

    @Test
    fun twoArgumentsTest() {
        val tokens = listOf(
            OperatorToken(Op.HYP, 0),
            OperatorToken(Op.LEFTP, 1),
            NumberToken(4, 2),
            OperatorToken(Op.SEPARATOR, 3),
            NumberToken(3, 4),
            OperatorToken(Op.RIGHTP, 5)
        )
        val range = Range(0, 5)
        val argumentInfo = parseArguments(range, tokens)
        assertThat(argumentInfo.tokens).hasSize(2)
        assertThat(argumentInfo.range).isEqualTo(Range(2, 4))
        assertThat(argumentInfo.nextPos).isEqualTo(6)
        assertThat(argumentInfo.push).isTrue()
        assertThat(argumentInfo.tokens[0]).isEqualTo(tokens[2])
        assertThat(argumentInfo.tokens[1]).isEqualTo(tokens[4])
    }

    @Test
    fun argumentWithOperatorSetsPushToFalseTest() {
        val tokens = listOf(
            OperatorToken(Op.SIN, 0),
            OperatorToken(Op.LEFTP, 1),
            NumberToken(3, 2),
            OperatorToken(Op.ADD, 3),
            NumberToken(5, 4),
            OperatorToken(Op.RIGHTP, 5)
        )
        val range = Range(0, 5)
        val argumentInfo = parseArguments(range, tokens)
        assertThat(argumentInfo.push).isFalse()
        assertThat(argumentInfo.range).isEqualTo(Range(2, 4))
        assertThat(argumentInfo.nextPos).isEqualTo(6)
    }
}