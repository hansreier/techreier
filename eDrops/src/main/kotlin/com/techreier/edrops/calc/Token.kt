package com.techreier.edrops.calc

import com.techreier.edrops.calc.utils.Range

sealed class Token(val position: Int)
data class NumberToken(val argument: Number, val pos: Int) : Token(pos)
data class OperatorToken(val operator: Op, val pos: Int) : Token(pos)

// Used to get the list of token to compare and or print
fun List<Token>.toTokenString(size: Int = 200): String {
    val cut = take(size)
    return cut.joinToString(separator = " | ", prefix = " ${cut.size} [ ", postfix = " ]") { token ->
        when (token) {
            is NumberToken -> token.argument.toString()
            is OperatorToken -> token.operator.abbrev()
        }
    }
}

// Reconstruct tokens for debugging purposes
fun List<Token>.toExpr(range: Range = Range(0, this.size - 1)): String {
    val expr = StringBuilder()
    if (range.min >= range.max) return ""
    for (i in range.min..range.max) {
        when (val token = this[i]) {
            is NumberToken -> expr.append(token.argument)
            is OperatorToken -> expr.append(token.operator.abbrev())
        }
        expr.append(' ')
    }
    return expr.toString()
}

fun Token.toTokenString(): String =
    when (this) {
        is NumberToken -> this.argument.toString()
        is OperatorToken -> this.operator.abbrev()
    }

fun Token.isAdd(): Boolean = this is OperatorToken && this.operator == Op.ADD
fun Token.isSubtract(): Boolean = this is OperatorToken && this.operator == Op.SUBTRACT
fun Token.isMultiply(): Boolean = this is OperatorToken && this.operator == Op.MULTIPLY
fun Token.isDivide(): Boolean = this is OperatorToken && this.operator == Op.DIVIDE
fun Token.isPow(): Boolean = this is OperatorToken && this.operator == Op.POW
fun Token.isLeftP(): Boolean = this is OperatorToken && this.operator == Op.LEFTP
fun Token.isRightP(): Boolean = this is OperatorToken && this.operator == Op.RIGHTP
fun Token.isSeparator(): Boolean = this is OperatorToken && this.operator == Op.SEPARATOR
fun Token.isVariable(): Boolean = this is OperatorToken && this.operator.isVariable()
fun Token.isFunction(): Boolean = this is OperatorToken && this.operator.isFunction()
fun Token.isOperator(): Boolean = this is OperatorToken && this.operator.isOperator()
fun Token.isSymbol(): Boolean = this is OperatorToken && this.operator.isSymbol()
fun Token.isValue(): Boolean = (this is OperatorToken && this.operator.isSymbol()) || this is NumberToken
fun Token.isNegate(): Boolean = (this is OperatorToken && this.operator == Op.NEGATE)


