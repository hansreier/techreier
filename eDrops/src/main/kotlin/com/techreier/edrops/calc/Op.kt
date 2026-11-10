package com.techreier.edrops.calc

import com.techreier.edrops.config.logger

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
    val abbrev: String,
    val noArgs: Int,
    val noResults: Int,
    val prior: Int,
) {
    GT(">", 2, 1, 6),
    LT("<", 2, 1, 6),
    GTE(">=", 2, 1, 6),
    LTE("<=", 2, 1, 6),
    EQ("==", 2, 1, 6),
    NEQ("!=", 2, 1, 6),
    ADD("+", 2, 1, 5),
    NOT("!", 1, 1, 7),
    AND("&&", 2, 1, 8),
    OR("||", 2, 1, 9),
    SUBTRACT("-", 2, 1, 5),
    MULTIPLY("*", 2, 1, 4),
    DIVIDE("/", 2, 1, 4),
    POW("^", 2, 1, 3),
    SEPARATOR(";", 2, 2, 10),
    ABS("abs", 1, 1 ),
    MAX("max", -1, 1),
    MIN("min", -1, 1),
    SELECT("select", 3 ,1),
    SIGN("sign", 1, 1 ),
    SQR("sqr", 1, 1 ),
    SQRT("sqrt", 1, 1 ),
    SUM("sum", -1, 1),
    SIN("sin", 1, 1),
    COS("cos", 1, 1),
    TAN("tan", 1, 1),
    ASIN("asin", 1, 1),
    ACOS("acos", 1, 1),
    ATAN("atan", 1, 1),
    RAD("rad", 1, 1),
    DEG("deg", 1, 1),
    NEGATE("negate", 1, 1),
    RADIANS("radians", 0, 0, -1),
    DEGREES("degrees", 0, 0, -1),
    EX("exp", 1, 1),
    LN("ln", 1, 1),
    LOG2("log2", 1, 1),
    LOG10("log10", 1, 1),
    POW10("pow10", 1, 1 ),
    HYP("hyp", 2, 1),
    GYP("gyp", 2, 1),
    E("e", 0, 1),
    C( "c",0,1),
    PI("pi", 0, 1),
    G("g", 0, 1),
    FALSE("false",   0,  1),
    TRUE("true",  0,  1),
    YX("yx", 2, 2, -1),
    RANDOM("random", 0, 1),
    REMOVE("r", 1, 0, -1),
    EMPTY("c", -1, 0, -1),
    DX("dx", 0,1 ),
    T("t", 0, 1),
    T0("t0", 0, 1),
    T1("t1", 0, 1),
    DT("dt", 0, 1),
    X("x", 0, 1),
    X0("x0", 0, 1),
    X1("x1", 0, 1),
    Y("y", 0, 1),
    Y0("y0", 0, 1),
    Y1("y1", 0, 1),
    DY("dy", 0, 1),
    Z("z", 0, 1),
    Z0("z0", 0, 1),
    Z1("z1", 0, 1),
    DZ("dz", 0, 1),
    U("u", 0, 1),
    U0("u0", 0, 1),
    U1("u1", 0, 1),
    DU("du", 0, 1),
    V("v", 0, 1),
    V0("v0", 0, 1),
    V1("v1", 0, 1),
    DV("dv", 0, 1),
    W("w", 0, 1),
    W0("w0", 0, 1),
    W1("w1", 0, 1),
    DW("dw", 0, 1),
    A("a", 0, 1),
    A0("a0", 0, 1),
    A1("a1", 0, 1),
    DA("da", 0, 1),
    M("m", 0, 1),
    F("f", 0, 1),
    K("k", 0, 1),
    P("p", 0, 1),
    Q("q", 0, 1),
    S("s", 0, 1),
    LEFTP("(", -1, 1, 1),
    RIGHTP(")", -1, 1, 1);

    constructor(abbrev: String, noArgs: Int, noResults: Int) : this(abbrev, noArgs, noResults, DEFAULT_LEVEL)

    fun abbrev(): String = abbrev
    fun noArgs(): Int = noArgs
    fun noResults(): Int = noResults
    fun prior(): Int = prior

    fun isOperator(): Boolean = basicOperators.contains(this)
    fun isParenthesis(): Boolean = parenthesis.contains(this)
    fun isConstant(): Boolean = constants.contains(this)
    fun isVariable(): Boolean = variables.contains(this)
    fun isSymbol(): Boolean = symbols.contains(this)
    fun isFunction(): Boolean = functions.contains(this)

    fun valid(set: Set<Op>): Boolean = set.contains(this)

    companion object {
        val basicOperators = setOf(ADD, SUBTRACT, MULTIPLY, DIVIDE, POW, SEPARATOR, GT, LT, GTE, LTE, EQ, NEQ, AND, OR)
        val unaryOperators = setOf(NEGATE, NOT)
        val parenthesis = setOf(LEFTP, RIGHTP)
        val variables = setOf(G, T, T0, T1, DT, X, X0, X1, Y, Y0, Y1, DY, Z, Z0, Z1, DZ, U, U0, U1, DU, V, V0, V1,
            DV, W, W0, W1, DW, A, A0, A1, DA, M, F, K, P, Q, S, DX)
        val constants = setOf(PI, E, C, TRUE, FALSE, RANDOM) // Functions without arguments is considered as a quasi-constant
        val symbols = variables + constants
        val functions = setOf(SIN, COS, TAN, ASIN, ACOS, ATAN, SQR, SQRT, DEG, EX, LN, LOG10, LOG2,
            POW10, HYP, GYP, RAD, ABS, SIGN, SUM, MAX, MIN, SELECT) + unaryOperators
        private const val DEFAULT_LEVEL = 2

        fun operator(abbrev: String): Op? {
            for (o in entries) {
                if (o.abbrev == abbrev.trim().lowercase()) {
                    return o
                }
            }
            return null
        }

        // Return operator based on abbreviation and set of operators
        fun operator(abbrev: String, set: Set<Op>): Op? {
            for (o in set) {
                if (o.abbrev == abbrev.trim().lowercase()) {
                    return o
                }
            }
            return null
        }

        // Return operator in text at expression position. Function used only in preparsing
        // At the moment separator is the only exception to rule (Removed before calculation)
        // TODO ReierAsk Only one return argument allowed. Any usecase for more?
        fun operator(text: String, pos: Int, set: Set<Op>): Op? {
            val textVar = text.substring(pos).lowercase()
            if (textVar.isNotEmpty()) {
                val validOps = set.filter { (it.prior >= 0 && it.noResults == 1) || it == SEPARATOR }

                val match = if (textVar[0].isLetter()) {
                    val word = textVar.takeWhile { it.isLetterOrDigit() }
                    validOps.find { it.abbrev == word }
                } else {
                    validOps
                        .filter { textVar.startsWith(it.abbrev) }
                        .maxByOrNull { it.abbrev.length }
                }

                if (match != null) return match
            }
            logger.info("Operator not found")
            return null
        }

        fun toString(set: Set<Op>): String {
            val allOp = StringBuilder()
            for (o in set) {
                allOp.append(o.abbrev).append(" ")
            }
            return allOp.toString()
        }
    }
}