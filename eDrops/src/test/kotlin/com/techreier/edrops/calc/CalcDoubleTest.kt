package com.techreier.edrops.calc

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.random.Random

class CalcDoubleTest {

    @Test
    fun addTest() {
        println("TestAddDouble")
        val calculator = CalcDouble(Double::class.javaObjectType)
        calculator.logStack = false
        calculator.enter(24.0)
        calculator.enter(26.0)
        calculator.op(Op.ADD)
        assertEquals(
            24.0 + 26.0 + 30.0,
            calculator.op(Op.ADD, 30.0) as Double,
            0.0001,
            "24 + 26 + 30"
        )
        calculator.logStack()
    }

    @Test
    fun randomAddTest() {
        val numbers = Array(10) {
            Random.nextDouble(0.0, 1000.0)
        }
        var sum = 0.0
        val expr = StringBuilder()

        val calculator = CalcDouble(Double::class.javaObjectType)
        calculator.logStack = false

        for (i in 0 until 10) {
            numbers[i] = Random.nextDouble(0.0, 1000.0)
            calculator.enter(numbers[i])
            if (i > 0) {
                expr.append("+")
                calculator.op(Op.ADD)
                sum += numbers[i]
            } else {
                sum = numbers[0]
            }
            expr.append(numbers[i])
        }
        assertEquals(sum, calculator.result() as Double, 0.0001, "Sum:$expr")
        calculator.logStack()
    }
}