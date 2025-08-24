package com.techreier.edrops.service

import org.junit.jupiter.api.Test
import com.techreier.edrops.config.logger
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull

class FractionServiceTest {

    private val fractionService = FractionService()

    @Test
    fun fractionTestSimple() {
        val fractionResult = fractionService.fraction(1.2)
        assertNull(fractionResult.error)
        logger.info("$fractionResult")
    }

    @Test
    fun fractionTestPi() {
        val fractionResult = fractionService.fraction(kotlin.math.PI, maxDeviation = 2e-3 )
        assertNull(fractionResult.error)
        assertEquals(22,fractionResult.numerator)
        assertEquals(7,fractionResult.denominator)
        logger.info("$fractionResult")
    }

    @Test
    fun fractionTestPiAccurate() {
        val fractionResult = fractionService.fraction(kotlin.math.PI, maxDeviation = 2e-6 )
        assertNull(fractionResult.error)
        logger.info("$fractionResult")
    }

    @Test
    fun fractionTestE() {
        val fractionResult = fractionService.fraction(kotlin.math.E, 1E-5, 1000)
        logger.info("$fractionResult")
    }

    @Test
    fun fractionTestE_macDenominator() {
        val fractionResult = fractionService.fraction(kotlin.math.E, 1E-6, 1000)
        logger.info("$fractionResult")
        assertEquals("error.maxDenominator", fractionResult.error)
        logger.info("$fractionResult")
    }

    @Test
    fun fractionTest_aritmethicException() {
        val fractionResult = fractionService.fraction(
            kotlin.math.sqrt(2.0), 0.0)
        logger.info("$fractionResult")
        assertEquals("error.arithmetic", fractionResult.error)
    }

    @Test
    fun fractionTestE_maxIterations() {
        val fractionResult = fractionService.fraction(
            kotlin.math.E, 1e-12, Long.MAX_VALUE, 3)
        logger.info("$fractionResult")
        assertEquals("error.maxIterations", fractionResult.error)
    }

}