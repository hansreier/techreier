package com.techreier.edrops.calc

import com.techreier.edrops.calc.utils.sanitize
import com.techreier.edrops.calc.utils.splitMultiExpr
import com.techreier.edrops.exceptions.ParseException
import com.techreier.edrops.util.Scope
import com.techreier.edrops.util.Trace

class MultiExpr<T : Any> (val calc: Calc<T>, multiExpr: String) {

    val multiExpr = multiExpr.sanitize()
    val exprList = mutableListOf<Expr<T>>()

    fun parse() {
        splitMultiExpr(multiExpr).forEach { (assignment, globalAssignmentOffset) ->
            val trimmed = assignment.trim()
            Trace.info(Scope.PARSE,"Multiexpr: $assignment")
            if (trimmed.isEmpty()) return@forEach
            if (trimmed.startsWith("//")) return@forEach

            val eqIndex = assignment.indexOf('=')
            if (eqIndex != -1) {
                val rawVarPart = assignment.substring(0, eqIndex)
                val variableName = rawVarPart.trim().lowercase()
                val existingOp = Op.entries.find { it.name.equals(variableName, ignoreCase = true) }

                val targetOp = if (existingOp == null) {
                    Trace.info(Scope.PARSE,"new variable: $variableName")
                    val newOp = VariableOp(variableName)
                    calc.set(newOp)
                    newOp
                } else {
                    existingOp
                }

                val exprText = assignment.substring(eqIndex + 1)
                val exprTextOffset = globalAssignmentOffset + eqIndex + 1

                try {
                    val expr = Expr(calc, exprText)
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
        Trace.info(Scope.CALC,"calculating multi expressions")
        exprList.forEach { expr ->
            Trace.info(Scope.CALC, "Expression: ${expr.expr}")
            expr.calculate()
            val target = expr.target
            val result = expr.result()
            Trace.info(Scope.CALC, "Resultat: $result")
            expr[target] = result
        }
    }

    operator fun set(op: Op, value: T) {
        calc[op] = value
    }

    operator fun get(op: Op): T {
        return calc[op]
    }
}
