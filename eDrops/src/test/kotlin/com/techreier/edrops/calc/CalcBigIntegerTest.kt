package com.techreier.edrops.calc

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.math.BigInteger
import kotlin.random.Random

class CalcBigIntegerTest {

    @Test
    fun addTest() {
        val calculator = CalcBigInteger(BigInteger::class.java)
        calculator.logStack = false
        calculator.enter(24)
        calculator.enter(26)
        calculator.op(Op.ADD)
        assertEquals(BigInteger.valueOf(24L + 26L + 30L),
            calculator.op(Op.ADD, BigInteger.valueOf(30)), "24 + 26 + 30")
        calculator.logStack()
    }

    @Test
    fun randomAddTest() {
        val numbers = Array(10) {
            BigInteger.valueOf(Random.nextLong(1000))
        }
        var sum = BigInteger.ZERO
        val expr = StringBuilder()

        val calculator = CalcBigInteger(BigInteger::class.java)
        calculator.logStack = false

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
        calculator.logStack()
    }
}