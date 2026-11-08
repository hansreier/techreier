package com.techreier.edrops.calc

import com.techreier.edrops.config.logger
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class OpTest {

    @Test
    fun operatorGroupsTest() {
        val calcOps = CalcDouble().operators
        val supportedFunctions = Op.functions.filter { it in calcOps }.toSet()

        logger.info(Op.toString(Op.basicOperators))
        logger.info(Op.toString(supportedFunctions))
        logger.info(Op.toString(Op.constants))
        logger.info(Op.toString(Op.parenthesis))
        logger.info(Op.toString(Op.variables))


        assertTrue(Op.basicOperators.isNotEmpty())
        assertTrue(supportedFunctions.isNotEmpty())
        assertTrue(Op.constants.isNotEmpty())
        assertTrue(Op.parenthesis.isNotEmpty())
        assertTrue(Op.variables.isNotEmpty())

        assertTrue(Op.toString(Op.basicOperators).isNotBlank())
        assertTrue(Op.toString(supportedFunctions).isNotBlank())
        assertTrue(Op.toString(Op.constants).isNotBlank())

        assertTrue(Op.SIN in supportedFunctions)
        assertTrue(Op.SELECT in supportedFunctions)
        assertTrue(Op.GT in calcOps)
        assertTrue(Op.GTE in calcOps)
        assertTrue(Op.EQ in calcOps)
        assertTrue(Op.NEQ in calcOps)

        assertTrue(Op.basicOperators.intersect(Op.functions).isEmpty())
    }

    @Test
    fun findComparisonOperatorsTest() {
        val ops = CalcDouble().operators
        assertEquals(Op.GTE, Op.operator(">= 5", 0, ops))
        assertEquals(Op.LTE, Op.operator("<= 5", 0, ops))
        assertEquals(Op.EQ, Op.operator("== 5", 0, ops))
        assertEquals(Op.NEQ, Op.operator("!= 5", 0, ops))
        assertEquals(Op.GT, Op.operator("> 5", 0, ops))
        assertEquals(Op.LT, Op.operator("< 5", 0, ops))
    }

    @Test
    fun findComparisonOperatorsWithoutSpaceTest() {
        val ops = CalcDouble().operators
        assertEquals(Op.GTE, Op.operator("10>=5", 2, ops))
        assertEquals(Op.LTE, Op.operator("10<=5", 2, ops))
        assertEquals(Op.EQ, Op.operator("10==5", 2, ops))
        assertEquals(Op.NEQ, Op.operator("10!=5", 2, ops))
        assertEquals(Op.GT, Op.operator("10>5", 2, ops))
        assertEquals(Op.LT, Op.operator("10<5", 2, ops))
    }

    @Test
    fun findFunctionAndCaseInsensitiveTest() {
        val ops = CalcDouble().operators
        assertEquals(Op.SIN, Op.operator("sin(1)", 0, ops))
        assertEquals(Op.SIN, Op.operator("SIN(1)", 0, ops))
        assertEquals(Op.SELECT, Op.operator("select(1;2;1)", 0, ops))
        assertEquals(Op.LOG2, Op.operator("log2(8)", 0, ops))
    }

    @Test
    fun partialWordShouldNotMatchFunctionTest() {
        val ops = CalcDouble().operators
        assertNull(Op.operator("sinus(1)", 0, ops))
        assertNull(Op.operator("selected", 0, ops))
    }

    @Test
    fun unknownOrEmptyInputReturnsNullTest() {
        val ops = CalcDouble().operators

        assertNull(Op.operator("", 0, ops))
        assertNull(Op.operator("123", 0, ops))
        assertNull(Op.operator("???", 0, ops))
        assertNull(Op.operator("abc", 3, ops))
    }

    @Test
    fun operatorPrecedenceOrderTest() {
        assertTrue(Op.GT.prior > Op.ADD.prior)
        assertTrue(Op.LT.prior > Op.ADD.prior)
        assertTrue(Op.GTE.prior > Op.ADD.prior)
        assertTrue(Op.LTE.prior > Op.ADD.prior)
        assertTrue(Op.EQ.prior > Op.ADD.prior)
        assertTrue(Op.NEQ.prior > Op.ADD.prior)
    }
}
