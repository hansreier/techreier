package com.techreier.edrops.calc

import com.techreier.edrops.calc.utils.sanitize
import com.techreier.edrops.calc.utils.splitMultiExpr
import com.techreier.edrops.exceptions.ParseException

class MultiExpr<T> (val calc: Calc<T>,  multiExpr: String) {

    val multiExpr = multiExpr.sanitize()
    val exprList = mutableListOf<Expr<T>>()

    fun parse() {
        splitMultiExpr(multiExpr).forEach { (assignment, globalAssignmentOffset) ->
            val trimmed = assignment.trim()
            if (trimmed.isEmpty()) return@forEach

            val eqIndex = assignment.indexOf('=')
            if (eqIndex != -1) {
                val rawVarPart = assignment.substring(0, eqIndex)
                val variableName = rawVarPart.trim().uppercase()

                val targetOp = try {
                    Op.valueOf(variableName)
                } catch (_: IllegalArgumentException) {
                    val varPos = globalAssignmentOffset + rawVarPart.indexOf(variableName.lowercase())
                    throw ParseException("Cannot parse variable", ParseError(VARIABLE_NOT_FOUND, varPos, variableName))
                }

                val exprText = assignment.substring(eqIndex + 1)

                val exprTextOffset = globalAssignmentOffset + eqIndex + 1

                try {
                    val expr = Expr(calc, exprText)
                    expr.trace(Trace.ALL)
                    expr.parse()
                    expr.target = targetOp
                    exprList.add(expr)
                } catch (e: ParseException) {
                    val adjustedError = ParseError(
                        e.parseError.key,
                        e.parseError.position + exprTextOffset,
                        e.parseError.oper
                    )
                    throw ParseException(e.message ?: "Parse error", adjustedError)
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
