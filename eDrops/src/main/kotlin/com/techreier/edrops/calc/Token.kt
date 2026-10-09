package com.techreier.edrops.calc

sealed class Token(val position: Int)
data class NumberToken(val argument: Number, val pos: Int) : Token(pos)
data class OperatorToken(val operator: Op, val pos: Int) : Token(pos)

// Used to get the list of token to compare and or print
fun List<Token>.toTokenString(size: Int = 200): String {
    val cut = take(size)
    return cut.joinToString(separator = " | ", prefix = " $size [ ", postfix = " ]") { token ->
        when (token) {
            is NumberToken -> token.argument.toString()
            is OperatorToken -> token.operator.abbrev()
        }
    }
}

