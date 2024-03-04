package com.techreier.edrops

import com.techreier.edrops.config.logger
import org.junit.jupiter.api.Test

const val MIN_SIZE = 1L
const val MAX_SIZE = 1000000L

class TestCollatz {

    @Test
    fun testCollatz() {
        for (start in MIN_SIZE..MAX_SIZE) {
            var value = start
            var iterations = 0
            do {
                iterations++
                if ((value % 2) == 0L) {
                    value /= 2
                } else {
                    value = value * 3 + 1
                }
        } while ((value != 1L))
            logger.info("seed $start is 1 after $iterations iterations")
        }
    }
}
