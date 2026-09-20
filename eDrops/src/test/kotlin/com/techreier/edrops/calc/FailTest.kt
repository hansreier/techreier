package com.techreier.edrops.calc


import org.junit.jupiter.api.Test

class MissingOperatorTest {

    @Test
    fun missingOperatorTest() {
        val input = "3x"
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
}
