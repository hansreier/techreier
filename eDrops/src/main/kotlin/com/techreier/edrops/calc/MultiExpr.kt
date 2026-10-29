package com.techreier.edrops.calc

import com.techreier.edrops.exceptions.ParseException

class MultiExpr<T> (val calc: Calc<T>,  multiExpr: String) {

    val multiExpr = multiExpr.trim()
    val exprList = mutableListOf<Expr<T>>()

    //TODO Exception handling is not good enough
    fun parse() {
        multiExpr.split(";").forEach { assignment ->
            val parts = assignment.split("=")
            if (parts.size == 2) {
                val variableName = parts[0].trim().uppercase()
                try {
                    val expr = Expr(calc, parts[1])
                    expr.trace(Trace.ALL)
                    expr.parse()
                    expr.target = Op.valueOf(variableName)
                    exprList.add(expr)
                } catch (_: IllegalArgumentException) { //TODO Add position later?
                    throw ParseException("Cannot parse number", ParseError(VARIABLE_NOT_FOUND, 0, variableName ))
                }
            }
        }
    }

    fun calculate() {
        exprList.forEach { expr ->
            expr.calculate()
            val target = expr.target
            expr[target] = expr.result()
        }
    }

    operator fun set(op: Op, value: T) {
        calc[op] = value
    }

    operator fun get(op: Op): T {
        return calc[op]
    }
}
