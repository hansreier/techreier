package com.techreier.edrops.calc

import com.techreier.edrops.calc.Op.Companion.DEFAULT_LEVEL

data class VarOp(
    override val abbrev: String,
    override val noArgs: Int = 0,
    override val noResults: Int = 1,
    override val prior: Int = DEFAULT_LEVEL
) : OpI {
    override fun isSymbol(): Boolean = true
    override fun isVariable(): Boolean = true
    override fun isOperator(): Boolean = false
    override fun isFunction(): Boolean = false
    override fun isConstant(): Boolean = false
}