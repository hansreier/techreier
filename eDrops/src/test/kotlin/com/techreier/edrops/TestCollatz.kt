package com.techreier.edrops

import com.techreier.edrops.config.logger
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

const val MAX_ITERATIONS = 500
const val MIN_SIZE = 10
const val MAX_SIZE = 100

class TestCollatz {
    @BeforeEach
    fun setUp() {
        logger.info("BeforeEach")
    }
    @Test
    fun testCollatz() {
        for (start in MIN_SIZE..MAX_SIZE) {
            var value = start
            var iterations = 0
            do {
                iterations++
                if (value % 2 == 0) {
                    value /= 2
                } else {
                    value = value * 3 + 1
                }
        } while ((value != 1) and (iterations <= MAX_ITERATIONS) )
            if (iterations > MAX_ITERATIONS) {
                logger.info("$MAX_ITERATIONS iterations exceeded, stopping")
                fail("not one")
            }
            logger.info("seed $start is 1 after $iterations iterations")
        }
    }
}
