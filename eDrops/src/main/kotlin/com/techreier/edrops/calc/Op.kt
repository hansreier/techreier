package com.techreier.edrops.calc

import java.util.EnumSet

/*
 * Operator parameters:
 *
 * abbreviation, number of arguments, number of results, priority (0/omitted = highest)
 *
 * Special values:
 * number of arguments = -n    :from -n to stack size
 *                     = -1000 : stack size (including zero)
 * priority = - 1 (not to be used in expressions by the parser
 */

enum class Op(
    private val abbrev: String,
    private val noArgs: Int,
    private val noResults: Int,
    private val prior: Int
) {
    ADD("+", 2, 1, 3),
    SUBTRACT("-", 2, 1, 3),
    MULTIPLY("*", 2, 1, 1),
    DIVIDE("/", 2, 1, 1),
    POW("^", 2, 1, 0),
    SEPARATOR(";",2,2,4),
    X2("^2", 1, 1, -1),
    SUM("sum", -1, 1, -1),
    SIN("sin", 1, 1),
    COS("cos", 1, 1),
    TAN("tan", 1, 1),
    ASIN("asin", 1, 1),
    ACOS("acos", 1, 1),
    ATAN("atan", 1, 1),
    RAD("rad", 1, 1),
    DEG("deg", 1, 1),
    RADIANS("radians", 0, 0, -1),
    DEGREES("degrees", 0, 0, -1),
    EX("e^", 1, 1),
    LN("ln", 1, 1),
    LOG10("log", 1, 1),
    POW10("10^", 1, 1, -1),
    HYP("hyp", 2, 1),
    GYP("gyp", 2, 1),
    E("e", 0, 1),
    PI("pi", 0, 1),
    YX("yx", 2, 2, -1),
    REMOVE("r", 1, 0, -1),
    EMPTY("c", -1, 0, -1),
    X("x", 0, 1),
    Y("y", 0, 1),
    Z("z", 0, 1),
    U("u", 0, 1),
    V("v", 0, 1),
    W("w", 0, 1),
    LEFTP("(", -1, 1, 4),
    RIGHTP(")", -1, 1, 4);

    constructor(abbrev: String, noArgs: Int, noResults: Int) : this(abbrev, noArgs, noResults, defaultLevel)

    fun abbrev(): String = abbrev
    fun noArgs(): Int = noArgs
    fun noResults(): Int = noResults
    fun prior(): Int = prior

    fun isBasic(): Boolean = basicOperators.contains(this)
    fun isParenthesis(): Boolean = parenthesis.contains(this)
    fun isOrdinary(): Boolean = !(basicOperators.contains(this) || parenthesis.contains(this))
    fun valid(set: EnumSet<Op>): Boolean = set.contains(this)

    companion object {
        private val basicOperators = EnumSet.of(ADD, SUBTRACT, MULTIPLY, DIVIDE, POW, SEPARATOR)
        private val parenthesis = EnumSet.of(LEFTP, RIGHTP)
        private const val defaultLevel = 2
        const val maxLevel = 4

        fun basicOperators(): EnumSet<Op> = basicOperators

        fun operator(abbrev: String): Op? {
            for (o in entries) {
                if (o.abbrev == abbrev.trim().lowercase()) {
                    return o
                }
            }
            return null
        }

        // Return operator based on abbreviation and enumSet of operators
        fun operator(abbrev: String, set: EnumSet<Op>): Op? {
            for (o in set) {
                if (o.abbrev == abbrev.trim().lowercase()) {
                    return o
                }
            }
            return null
        }

        // Return operator in text at expression position. Only one return value is accepted
        // TODO ReierAsk really not difficult to implemtent general x argument return
        fun operator(text: String, pos: Int, set: EnumSet<Op>): Op? {
            val textVar = text.substring(pos).lowercase()
            if (textVar.isNotEmpty()) {
                for (o in set) {
                    if ((o.prior >= 0) && (o.noResults == 1) || ( o == SEPARATOR)) {
                        if (textVar.startsWith(o.abbrev)) {
                            val p = o.abbrev.length
                            if (textVar.length <= p)
                                return o
                            else {
                                val c = textVar[p]
                                when (o.abbrev) {
                                    "(", ")", "^", "+", "-", "/", "*", SEPARATOR.abbrev -> return o
                                    else -> {
                                        if (!(Character.isLetter(c) || Character.isDigit(c))) {
                                            return o
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return null
        }

        fun toString(set: EnumSet<Op>): String {
            val allOp = StringBuilder()
            for (o in set) {
                allOp.append(o.abbrev).append(" ")
            }
            return allOp.toString()
        }
    }
}