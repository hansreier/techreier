package com.techreier.edrops.calc


import com.techreier.edrops.config.logger
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class MissingOperatorTest {

    @Test
    fun missingOperatorTest() { //TODO Reier almost correct.
        val input = "3x + 5x^2"
        val xVal = 1.0

        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val expr = Expr(calculator)
        expr.traceLevel(Trace.ALL)
        val parsed = expr.parse(input)
        assertTrue(parsed)
        calculator.variables.add(xVal)
        expr.calculate()
        val result = calculator.result()
        logger.info("input=$input, x=$xVal calculated=$result")
    }

    @Test
    fun normalExpressionWithImplicitMultiplicationTest() {
        val input = "5 * (2 + 3x)"
        val xVal = 4.0

        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val expr = Expr(calculator)

        val parsed = expr.parse(input)
        println("Parseren godtok '$input': $parsed")

        if (parsed) {
            calculator.variables.add(xVal)
            expr.calculate()

            val result = calculator.result()
            println("Resultat fra stakken: $result")
        }
    }

    @Test
    fun decimalCommaTest() {
        val input = "5 * 2,5" // Tester norsk desimaltall med komma

        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val expr = Expr(calculator)

        val parsed = expr.parse(input)
        println("Parseren godtok desimal-komma i '$input': $parsed")

        if (parsed) {
            expr.calculate()
            val result = calculator.result()
            println("Resultat: $result") // Skal ideelt sett bli 12.5
        }
    }

    @Test
    fun strangeNumberTest() {
        val input = "5e0 * 2.5"

        val calculator: Calc<Double> = CalcDouble(Double::class.javaObjectType, true)
        val expr = Expr(calculator)

        val parsed = expr.parse(input)
        assertFalse(parsed)
    }
}
