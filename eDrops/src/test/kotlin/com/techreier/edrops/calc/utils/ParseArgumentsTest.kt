package com.techreier.edrops.calc.utils

import com.techreier.edrops.calc.NumberToken
import com.techreier.edrops.calc.Op
import com.techreier.edrops.calc.OperatorToken
import com.techreier.edrops.util.Trace.debug
import com.techreier.edrops.util.Trace.level
import com.techreier.edrops.util.TraceLevel
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ParseArgumentsTest {

    @BeforeEach
    fun setTrace() {
        level = TraceLevel.DEBUG
        debug("")
    }

    @Test
    fun oneArgumentNoParentheisTest() { // Input: A function as unary operator
        val tokens = listOf(
            OperatorToken(Op.NOT, 0),
            OperatorToken(Op.X, 1),
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
        assertThat(argumentInfo.range).isEqualTo(Range(2, 4))
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