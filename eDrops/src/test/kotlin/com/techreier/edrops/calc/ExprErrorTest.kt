package com.techreier.edrops.calc

import com.techreier.edrops.util.Trace
import com.techreier.edrops.calc.utils.errorIndicator
import com.techreier.edrops.exceptions.ParseException
import com.techreier.edrops.util.TraceLevel
import com.techreier.edrops.util.TraceScope
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class ExprErrorTest {

    lateinit var calc: CalcDouble

    @BeforeEach
    fun setUp() {
        calc = CalcDouble()
        Trace.level = TraceLevel.INFO
        Trace.trace("")
    }

    @Test
    fun negativeNumberPower2FailsTest() {
        val input = "3 -2^2"
        val expr = Expr(calc, input)
        val e = assertThrows <ParseException> { expr.parse() }
        assertEquals(MISSING_OPERATOR, e.parseError.key)
    }

    @Test
    fun minusNegateTest() {  //Returns error
        val expr = Expr(calc, "--(3+5)")
        val e = assertThrows <ParseException> { expr.parse() }
        assertEquals(CONSECUTIVE_NEGATE, e.parseError.key)
    }

    @Test
    fun unicodeTest() {
        val input = "a\u200B + 2"
        val expr = Expr(calc, input)
        val e = assertThrows <ParseException> { expr.parse() }
        assertThat(e.parseError.key).isEqualTo(UNPARSEABLE)
    }

    @Test
    fun repeatedOperatorFailsTest() {
        val input = "2^^3"
        val expr = Expr(calc, input)
        val e = assertThrows <ParseException> { expr.parse() }
        assertThat(e.parseError.key).isEqualTo(MISSING_OPERAND)
        val errorIndicator = errorIndicator(expr.expr, e.parseError)
        Trace.trace(TraceScope.PARSE,"Error indicator: $errorIndicator")
        assertEquals( "2^???^3", errorIndicator)
    }

    @Test
    fun emptyTest() {
        val input = ""
        val expr = Expr(calc, input)
        val e = assertThrows <ParseException> {
            expr.parse()
        }
        Trace.error(TraceScope.PARSE,e.message)
        assertThat(e.parseError.key).isEqualTo(UNPARSEABLE)
    }

}
