package com.techreier.edrops.calc

import com.techreier.edrops.calc.utils.errorText
import com.techreier.edrops.util.Trace.debug
import com.techreier.edrops.util.Trace.info
import com.techreier.edrops.util.Scope
import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.math.min

open class TestBase() {

    protected fun assertPreparse(expectedTokens: ArrayList<Token>, calc: Calc<*>, input: String, size: Int= 1000) {
        val result = preparse(calc, input)
        val actualTokens = result.tokens
        val actualTokenString = actualTokens.toTokenString(min(size, actualTokens.size))
        val actual = result.parseError?.key ?: actualTokenString
        val expected = expectedTokens.toTokenString(min(size, expectedTokens.size))
        debug(actualTokenString)
        assertEquals(expected, errorText(input, result.parseError) ?: actual ,input)
    }

    protected fun assertPreparseFails(errorKey: String, calc: Calc<*>, input: String, size: Int = 1000) {
        val result = preparse(calc, input)
        val actualTokens = result.tokens
        info(Scope.PARSE,errorText(input, result.parseError))
        info(Scope.PARSE, actualTokens.toTokenString(min(size, actualTokens.size)))
        assertEquals(errorKey, result.parseError?.key, input)
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
