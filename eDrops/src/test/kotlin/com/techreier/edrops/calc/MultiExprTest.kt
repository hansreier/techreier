package com.techreier.edrops.calc

import com.techreier.edrops.util.Trace
import com.techreier.edrops.calc.utils.errorIndicator
import com.techreier.edrops.calc.utils.sanitize
import com.techreier.edrops.exceptions.ParseException
import com.techreier.edrops.util.TraceLevel
import com.techreier.edrops.util.TraceScope
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class MultiExprTest {

    val calc = CalcDouble()

    @BeforeEach
    fun setUp() {
        Trace.level = TraceLevel.TRACE
        Trace.trace("")
    }

    @Test
    fun happyTest() {

        val input = """
            x = 5;
            y = 3 * x;
            z = x + y;
        """.trimIndent()

        val expr = MultiExpr(calc, input)
        expr.parse()
        expr.calculate()

        assertEquals(5.0, expr[Op.X])
        assertEquals(15.0, expr[Op.Y])
        assertEquals(20.0, expr[Op.Z])
    }

    @Test
    fun parseErrorTest() {

        val input = """
            x = 5;
            y = 3 * x-3;
            z = x + y;
        """.trimIndent()


        val e = assertThrows<ParseException> {
            val expr = MultiExpr(calc, input)
            expr.parse()
        }
        val errorIndicator = errorIndicator(input.sanitize(), e.parseError)
        Trace.info(errorIndicator)
        assertEquals("x = 5; y = 3 * ???x-3; z = x + y;", errorIndicator )
    }

    @Test
    fun repeatedCalcTest() {
        val startValues = """
           dx = 0.1;
           x = 0;
        """.trimIndent()

        val start = MultiExpr(calc, startValues)
        start.parse()
        start.calculate()

        val input = """
           y = sin(x) + cos(x);
           x = x + dx;
        """.trimIndent()

        val expr = MultiExpr(calc, input)
        expr.parse()

        do {
            expr.calculate()
            Trace.trace(TraceScope.CALC,"x=${calc[Op.X]}, y=${calc[Op.Y]}")
        } while (calc[Op.X] < 3)

    }

}