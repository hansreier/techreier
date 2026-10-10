package com.techreier.edrops.calc

import com.techreier.edrops.config.logger
import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.math.min

open class TestBase() {

    protected fun assertPreparse(expectedTokens: ArrayList<Token>, expr: Expr, size: Int= 1000) {
        expr.preparse()
        val actualTokens = expr.xTokens
        val actualTokenString = actualTokens.toTokenString(min(size, actualTokens.size))
        val actual = expr.parseError?.key ?: actualTokenString
        val expected = expectedTokens.toTokenString(min(size, expectedTokens.size))
        logger.debug(actualTokenString)
        assertEquals(expected, expr.errorText() ?: actual , expr.expr)
    }

    protected fun assertPreparseFails(errorKey: String, expr: Expr, size: Int = 1000) {
        expr.preparse()
        val errorFound = expr.parseError
        val actualTokens = expr.xTokens
        logger.info(expr.errorText())
        logger.info(actualTokens.toTokenString(min(size, actualTokens.size)))
        assertEquals(errorKey, errorFound?.key, expr.expr)
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
