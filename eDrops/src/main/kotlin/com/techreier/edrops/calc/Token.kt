package com.techreier.edrops.calc

sealed class Token(val position: Int)
data class NumberToken(val argument: Number, val pos: Int) : Token(pos)
data class OperatorToken(val operator: Op, val pos: Int) : Token(pos)
