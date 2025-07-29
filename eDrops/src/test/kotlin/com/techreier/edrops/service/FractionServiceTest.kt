package com.techreier.edrops.service

import org.junit.jupiter.api.Test
import com.techreier.edrops.config.logger

class FractionServiceTest {

    private val fractionService = FractionService()

    @Test
    fun fractionTestPi() {
        val fraction = fractionService.fraction(kotlin.math.PI, epsilon = 2e-3 )
        logger.info("$fraction")
    }

    @Test
    fun fractionTestPiAccurate() {
        val fraction = fractionService.fraction(kotlin.math.PI, epsilon = 2e-6 )
        logger.info("$fraction")
    }

    @Test
    fun fractionTestSimple() {
        val fraction = fractionService.fraction(1.2)
        logger.info("$fraction")
    }

}