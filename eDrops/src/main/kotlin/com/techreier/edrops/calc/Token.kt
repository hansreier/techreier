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

fun Token.isAdd(): Boolean = this is OperatorToken && this.operator == Op.ADD
fun Token.isSubtract(): Boolean = this is OperatorToken && this.operator == Op.SUBTRACT
fun Token.isMultiply(): Boolean = this is OperatorToken && this.operator == Op.MULTIPLY
fun Token.isDivide(): Boolean = this is OperatorToken && this.operator == Op.DIVIDE
fun Token.isPow(): Boolean = this is OperatorToken && this.operator == Op.POW
fun Token.isLeftP(): Boolean = this is OperatorToken && this.operator == Op.LEFTP
fun Token.isRightP(): Boolean = this is OperatorToken && this.operator == Op.RIGHTP
fun Token.isSeparator(): Boolean = this is OperatorToken && this.operator == Op.SEPARATOR

