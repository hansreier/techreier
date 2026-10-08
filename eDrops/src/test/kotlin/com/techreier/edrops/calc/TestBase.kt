package com.techreier.edrops.calc

import com.techreier.edrops.config.logger
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue

open class TestBase() {

    protected fun assertPreparseFails(expr: Expr) {
        assertFalse(expr.preparse(), expr.expr)
        logger.info(errorText(expr))
    }

    protected fun errorText(expr: Expr): String {
        val errorText = StringBuilder()
        expr.parseErrors.forEach { err ->
            val indicator = expr.errorIndicator(err.position)
            val operText = if (err.oper.isBlank()) "" else "${err.oper} "
            val errText = "pos=${err.position} op=$operText key=${err.key} ${indicator}"
            errorText.appendLine(errText)
        }
        return errorText.toString()
    }

    protected fun assertPreparse(expectedTokens: ArrayList<Token>, expr: Expr, strict: Boolean = true) {
        expr.preparse()
        val actualTokens = expr.xTokens
        assertFalse(expr.parseErrors.isNotEmpty(), errorText(expr))

        if (strict) {

            val actual = actualTokens.map { token ->
                when (token) {
                    is NumberToken -> TestToken(operator = null, number = token.argument)
                    is OperatorToken -> TestToken(operator = token.operator, number = null)
                }
            }

            val expected = expectedTokens.map { token ->
                when (token) {
                    is NumberToken -> TestToken(operator = null, number = token.argument)
                    is OperatorToken -> TestToken(operator = token.operator, number = null)
                }
            }


            assertEquals(expected, actual)

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
            assertEquals(expected::class, actual::class, "tokentype differs at index $index")

            when (expected) {
                is NumberToken -> {
                    actual as NumberToken
                    assertEquals(
                        expected.argument,
                        actual.argument,
                        "argument differs \n " + expr.errorIndicator(actual.position)
                    )
                }
                is OperatorToken -> {
                    actual as OperatorToken
                    assertEquals(
                        expected.operator,
                        actual.operator,
                        "operator differs  \n " + expr.errorIndicator(actual.position)
                    )
                }
            }
        }
    }

    protected fun tokens(vararg items: Any): ArrayList<Token> {
        val mappedTokens = items.mapIndexed { index, item ->
            when (item) {
                is Number -> NumberToken(item.toDouble(), index)
                is Op -> OperatorToken(item, index)
                else -> throw IllegalArgumentException("Ukjent type i token-listen: ${item::class}")
            }
        }
        return ArrayList(mappedTokens)
    }

    data class TestToken(val operator: Op?, val number: Number?)

}
