package com.techreier.edrops.calc

import com.techreier.edrops.exceptions.ParseException
import com.techreier.edrops.util.Trace
import com.techreier.edrops.util.TraceLevel
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.Double.Companion.NaN
import kotlin.test.assertEquals

class ExprLogicTest {

    lateinit var calc: CalcDouble

    @BeforeEach
    fun setUp() {
        calc = CalcDouble()
        Trace.level = TraceLevel.TRACE
        Trace.trace("")
    }

    @Test
    fun selectTrueFalseTest() {
        val input = "select(select(x; true; false) ;false ;true)"
        val expr = Expr(calc, input)
        expr[Op.X] = TRUE
        expr.parse()
        expr.calculate()
        assertEquals(FALSE, expr.result(), 1e-9)
    }

    @Test
    fun selectNaNTest() {
        val input = "select(5; 3; 2)"
        val expr = Expr(calc, input)
        expr[Op.X] = TRUE
        expr.parse()
        expr.calculate()
        assertEquals(NaN, expr.result())
    }

    @Test
    fun greaterThanTrueTest() {
        val input = "5 > 3"
        val expr = Expr(calc, input)
        expr.parse()
        expr.calculate()
        assertEquals(TRUE, expr.result())
    }

    @Test
    fun greaterThanFalseTest() {
        val input = "3 > 5"
        val expr = Expr(calc, input)
        expr.parse()
        expr.calculate()
        assertEquals(FALSE, expr.result())
    }

    @Test
    fun lessThanTrueTest() {
        val input = "3 < 5"
        val expr = Expr(calc, input)
        expr.parse()
        expr.calculate()
        assertEquals(TRUE, expr.result())
    }

    @Test
    fun lessThanFalseTest() {
        val input = "5 < 3"
        val expr = Expr(calc, input)
        expr.parse()
        expr.calculate()
        assertEquals(FALSE, expr.result())
    }

    @Test
    fun greaterThanOrEqualEqualTest() {
        val input = "5 >= 5"
        val expr = Expr(calc, input)
        expr.parse()
        expr.calculate()
        assertEquals(TRUE, expr.result())
    }

    @Test
    fun greaterThanOrEqualFalseTest() {
        val input = "4 >= 5"
        val expr = Expr(calc, input)
        expr.parse()
        expr.calculate()
        assertEquals(FALSE, expr.result())
    }

    @Test
    fun lessThanOrEqualEqualTest() {
        val input = "5 <= 5"
        val expr = Expr(calc, input)
        expr.parse()
        expr.calculate()
        assertEquals(TRUE, expr.result())
    }

    @Test
    fun lessThanOrEqualFalseTest() {
        val input = "6 <= 5"
        val expr = Expr(calc, input)
        expr.parse()
        expr.calculate()
        assertEquals(FALSE, expr.result())
    }

    @Test
    fun equalPrecisionTest() {
        val input = "0.1 + 0.2 == 0.3"
        val expr = Expr(calc, input)
        expr.parse()
        expr.calculate()
        assertEquals(TRUE, expr.result())
    }

    @Test
    fun notEqualTrueTest() {
        val input = "5 != 3"
        val expr = Expr(calc, input)
        expr.parse()
        expr.calculate()
        assertEquals(TRUE, expr.result())
    }

    @Test
    fun notEqualPrecisionFalseTest() {
        val input = "0.1 + 0.2 != 0.3"
        val expr = Expr(calc, input)
        expr.parse()
        expr.calculate()
        assertEquals(FALSE, expr.result())
    }

    @Test
    fun selectWithComparisonTrueTest() {
        val input = "select(5 > 3; 10; 20)"
        val expr = Expr(calc, input)
        expr.parse()
        expr.calculate()
        assertEquals(10.0, expr.result())
    }

    @Test
    fun selectWithComparisonFalseTest() {
        val input = "select(0.1 + 0.2 != 0.3; 10; 20)"
        val expr = Expr(calc, input)
        expr.parse()
        expr.calculate()
        assertEquals(20.0, expr.result())
    }

    @Test
    fun selectWithGreaterThanTrueTest() {
        val input = "select(5 > 3; 10; 20)"
        val expr = Expr(calc, input)
        expr.parse()
        expr.calculate()
        assertEquals(10.0, expr.result())
    }

    @Test
    fun relationalOperatorsWithToleranceTest() {
        val input = "0.1 + 0.2 == 0.3"
        val expr = Expr(calc, input)
        expr.parse()
        expr.calculate()
        assertEquals(1.0, expr.result())
    }

    @Test
    fun logicalAndValidTest() {
        val input = "true && true"
        val expr = Expr(calc, input)
        expr.parse()
        expr.calculate()
        assertEquals(TRUE, expr.result())
    }

    @Test
    fun logicalOrTest() {
        val input = "false || true"
        val expr = Expr(calc, input)
        expr.parse()
        expr.calculate()
        assertEquals(TRUE, expr.result())
    }

    @Test
    fun logicalAndInvalidInputNanTest() {
        val input = "true && 5"
        val expr = Expr(calc, input)
        expr.parse()
        expr.calculate()
        assertTrue(expr.result().isNaN())
    }

    @Test
    fun relationalOperatorNanPropagationTest() {
        val input = "(0 / 0) > 3"
        val expr = Expr(calc, input)
        expr.parse()
        expr.calculate()
        assertTrue(expr.result().isNaN())
    }

    @Test
    fun complexLogicalAndSelectTest() {
        val input = "select((10 >= 5) && (2 < 3); 100; 200)"
        val expr = Expr(calc, input)
        expr.parse()
        expr.calculate()
        assertEquals(100.0, expr.result())
    }

    @Test
    fun notTest() {
        val expr = Expr(calc, "!(!x)")
        expr[Op.X] = TRUE
        expr.parse()
        expr.calculate()
        assertEquals(TRUE , calc.result())
    }

    @Test
    fun notAdvancedTest() {
        val expr = Expr(calc, "!x + 5")
        expr[Op.X] = TRUE
        expr.parse()
        expr.calculate()
        assertEquals(5.0 , calc.result())
    }

    @Test
    fun notAtEndTest() {
        val expr = Expr(calc, " 3 !")
        expr[Op.X] = TRUE
        val e = assertThrows(ParseException::class.java) { expr.parse() }
        assertEquals(MISSING_ARGUMENTS , e.parseError.key)
    }

    @Test
    fun relationalAsymmetryTest() {
        val expr1 = Expr(calc, "3 < 5")
        expr1.parse()
        expr1.calculate()
        assertEquals(TRUE, expr1.result())

        val expr2 = Expr(calc, "5 < 3")
        expr2.parse()
        expr2.calculate()
        assertEquals(FALSE, expr2.result())
    }

    @Test
    fun logicNaNTest() {
        val input = "sin(x) && true"
        val expr = Expr(calc, input)
        expr[Op.X] = 3.0
        expr.parse()
        expr.calculate()
        assertEquals(NaN, expr.result())
    }

}
