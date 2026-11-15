package com.techreier.edrops.calc

sealed interface OpI {
    val abbrev: String
    val noArgs: Int
    val noResults: Int
    val prior: Int

    fun isVariable(): Boolean = false
    fun isParenthesis(): Boolean = false
    fun isFunction(): Boolean = false
    fun isOperator(): Boolean = false
    fun isSymbol(): Boolean = false
    fun isConstant(): Boolean = false
}