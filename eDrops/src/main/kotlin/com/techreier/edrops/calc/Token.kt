package com.techreier.edrops.calc

data class Token(
    var type: TokenType,
    var operator: Op? = null,
    var argument: Number? = null,
    var position: Int
) {
    constructor(operator: Op, position: Int) : this(
        type = TokenType.OPERATOR,
        operator = operator,
        argument = null,
        position = position
    )

    constructor(oper: Oper) : this(
        type = TokenType.OPERATOR,
        operator = oper.op,
        argument = null,
        position = oper.pos
    )

    constructor(argument: Number, position: Int) : this(
        type = TokenType.NUMBER,
        operator = null,
        argument = argument,
        position = position
    )

    override fun toString(): String {
        return if (type == TokenType.NUMBER) {
            "$argument[$position]"
        } else {
            "$operator[$position]"
        }
    }

}
