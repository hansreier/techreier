package com.techreier.edrops.calc

// TODO ReierAsk Can probably be simplified or changed. Is replaces really by Token class
data class Oper(
    val op: Op,
    val pos: Int,
    var level: Int = 0,
) {

    fun noArgs(): Int = op.noArgs()

    fun abbrev(): String = op.abbrev()

    fun level(): Int = level

    fun level(level: Int) {
        this.level = level
    }

    fun isBasic(): Boolean = op.isOperator()

    fun isOrdinary(): Boolean = op.isOrdinary()

    fun isParenthesis(): Boolean = op.isParenthesis()

    override fun toString(): String {
        return "$op[$pos]"
    }

}