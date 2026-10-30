package com.techreier.edrops.calc

import com.techreier.edrops.calc.utils.errorIndicator
import com.techreier.edrops.calc.utils.sanitize
import com.techreier.edrops.config.logger
import com.techreier.edrops.exceptions.ParseException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.math.cos
import kotlin.math.sin

class MultiExprTest {

    val calc = CalcDouble()

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
        logger.info(errorIndicator)
        assertEquals("x = 5; y = 3 * x???-3; z = x + y;", errorIndicator )
    }

    @Test
    fun repeatedCalcTest() {

        val input = """
           dx = 0.1;
           x = 3;
           y = sin(x) + cos(x);
           x = x + dx;
        """.trimIndent()

        val expr = MultiExpr(calc, input)
        calc.degrees = false
        expr.parse()
        logger.info("ReierAsk parsed")
        expr.calculate()

        assertEquals(0.1, expr[Op.DX])
        assertEquals(3.1, expr[Op.X])
        assertEquals(sin(3.0) + cos(3.0), expr[Op.Y], 1e-9)
    }

}