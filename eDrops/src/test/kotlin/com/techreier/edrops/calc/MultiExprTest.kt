package com.techreier.edrops.calc

import com.techreier.edrops.util.Trace
import com.techreier.edrops.calc.utils.errorIndicator
import com.techreier.edrops.calc.utils.sanitize
import com.techreier.edrops.exceptions.ParseException
import com.techreier.edrops.util.Trace.level
import com.techreier.edrops.util.Trace.trace
import com.techreier.edrops.util.TraceLevel
import com.techreier.edrops.util.Scope
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class MultiExprTest {

    val calc = CalcDouble()

    @BeforeEach
    fun setUp() {
        level = TraceLevel.INFO
        trace("")
        calc.radians = false
    }

    @Test
    fun happyTest() {

        val input = """
            // A very simple happy expression to parse;
            x = 5;
            // And then we calculate y og z;
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
    fun happyOneExpressionTest() {

        val input = """
            // A very simple happy expression to parse;
            x = sin(90)+cos(90)
        """.trimIndent()

        val expr = MultiExpr(calc, input)
        expr.parse()
        expr.calculate()

        assertEquals(1.0, expr[Op.X])
    }

    @Test
    fun noAssigmentTest() {

        val input = "sin(90)"

        val expr = MultiExpr(calc, input)
        expr.parse()
        expr.calculate()

        assertEquals(1.0, expr[Op.RESULT])

    }

    @Test
    fun noAssignmentMultiExpressionTest() {

        val input = """
            x = 90;
            sin(x);
            result+3
        """.trimIndent()

        val expr = MultiExpr(calc, input)
        expr.parse()
        expr.calculate()

        assertEquals(4.0, expr[Op.RESULT])

    }

    @Test
    fun semiColonForgottenTest() {

        val input = """
            x = 5
            y = 3 * x
            z = x + y
        """.trimIndent()

        val expr = MultiExpr(calc, input)
        assertThrows<ParseException> { expr.parse() }
    }

    @Test
    fun userDefinedVariableTest() {

        val input = """
            // A very simple happy expression to parse;
            y = 1;
            reier = 4  + y;
            reier = reier;
            hans = reier + 3;
            z = hans + reier + y;
        """.trimIndent()

        val expr = MultiExpr(calc, input)

        expr.parse()
        expr.calculate()
        assertEquals(1.0, expr[Op.Y])
        assertEquals(5.0, expr[VarOp("reier")])
        assertEquals(8.0, expr[VarOp("hans")])
        assertEquals(14.0, expr[Op.Z])
    }

    @Test
    fun illegalUserDefinedVariableTest() {

        val input = """
            // Should really fail;
            y = 1;
            sin = 4  + y;
            hans = sin + 3;
          
        """.trimIndent()

        val e = assertThrows<ParseException> {
            val expr = MultiExpr(calc, input)
            expr.parse()
        }
        val errorIndicator = errorIndicator(input.sanitize(), e.parseError)
        Trace.info(errorIndicator)
        assertEquals(ILLEGAL_VARIABLES, e.parseError.key)
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
            trace(Scope.CALC,"x=${calc[Op.X]}, y=${calc[Op.Y]}")
        } while (calc[Op.X] < 3)

    }

}