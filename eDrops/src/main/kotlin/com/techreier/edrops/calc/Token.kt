package com.techreier.edrops.calc

data class Token(
    val type: TokenType,
    private val rawOperator: Op? = null,
    private val rawArgument: Number? = null,
    val position: Int
) {
    val operator: Op
        get() = rawOperator ?: throw IllegalStateException("Token er ikke en operator i posisjon $position")

    val argument: Number
        get() = rawArgument ?: throw IllegalStateException("Token er ikke et tall i posisjon $position")

    constructor(operator: Op, position: Int) : this(
        type = TokenType.OPERATOR,
        rawOperator = operator,
        rawArgument = null,
        position = position
    )

    constructor(oper: Oper) : this(
        type = TokenType.OPERATOR,
        rawOperator = oper.op,
        rawArgument = null,
        position = oper.pos
    )

    constructor(argument: Number, position: Int) : this(
        type = TokenType.NUMBER,
        rawOperator = null,
        rawArgument = argument,
        position = position
    )

    override fun toString(): String {
        return if (type == TokenType.NUMBER) {
            "$rawArgument[$position]"
        } else {
            "$rawOperator[$position]"
        }
    }
}
