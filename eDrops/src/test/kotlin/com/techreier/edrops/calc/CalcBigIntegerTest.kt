package com.techreier.edrops.calc

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.math.BigInteger
import kotlin.random.Random

class CalcBigIntegerTest {

    @Test
    fun addTest() {
        val calc = CalcBigInteger()
        calc.trace = Trace.ALL
        calc.enter(24)
        calc.enter(26)
        calc.enter(30)
        calc.op(Op.ADD)
        calc.op(Op.ADD)
        assertEquals(80L.toBigInteger(), calc.result(), "24 + 26 + 30")
    }

    @Test
    fun randomAddTest() {
        val numbers = Array(10) {
            BigInteger.valueOf(Random.nextLong(1000))
        }
        var sum = BigInteger.ZERO
        val expr = StringBuilder()

        val calculator = CalcBigInteger()

        for (i in 0 until 10) {
            numbers[i] = BigInteger.valueOf(Random.nextInt(1000).toLong())
            calculator.enter(numbers[i])
            if (i > 0) {
                expr.append("+")
                calculator.op(Op.ADD)
                sum = sum.add(numbers[i])
            } else {
                sum = numbers[0]
            }
            expr.append(numbers[i])
        }
        assertEquals(sum, calculator.result(), "Sum:$expr")
        calculator.traceStack()
    }
}