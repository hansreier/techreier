package com.techreier.edrops.calc

import com.techreier.edrops.config.logger
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import kotlin.math.min

open class TestBase() {

    protected fun assertPreparse(expectedTokens: ArrayList<Token>, expr: Expr, size: Int= 1000) {
        expr.preparse()
        val actualTokens = expr.xTokens
        val actual = actualTokens.toTokenString(min(size, actualTokens.size))
        val expected = expectedTokens.toTokenString(min(size, expectedTokens.size))
        assertFalse(expr.parseErrors.isNotEmpty(),
            "${errorText(expr)}\n$actual")
        assertEquals(expected, actual)
        logger.debug(actual)
    }

    protected fun assertPreparseFails(expr: Expr, size: Int = 1000) {
        assertFalse(expr.preparse(), expr.expr)
        val actualTokens = expr.xTokens
        logger.info(actualTokens.toTokenString(min(size, actualTokens.size)))
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

}
