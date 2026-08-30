package com.techreier.edrops.graph

import com.techreier.edrops.config.logger
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class TicsTest {

    // Manual inspection is best for veritying nice numbers used on grapth axis
    @Disabled
    @Test
    fun testNiceNumbersSweep() {
        var interval = 10.0
        repeat(20) {
            logger.info("--- interval = $interval ---")
            for (noTics in 3..10) {
                val niceNumber = niceNumber(0.0, interval, noTics)
                logger.info("noTics=$noTics -> niceNumber = $niceNumber")
            }
            interval += 10.0
        }
    }

    @Disabled
    @Test
    fun testNiceNumberFloatingPointEdgeCases() {
        // Tester tall som er beryktede for flyttallsfeil, f.eks. like under hele tall eller rare desimaler
        val trickyIntervals = listOf(
            9.999999999,
            10.000000001,
            29.999999999,
            0.3 + 0.6 * 10 // Klassisk flyttallsfelle (2.9999999999999996 istedenfor 3.0)
        )

        for (interval in trickyIntervals) {
            logger.info("--- interval = $interval ---")
            for (noTics in 3..10) {
                val niceNumber = niceNumber(0.0, interval, noTics)
                logger.info("tricky interval=$interval, noTics=$noTics -> niceNumber = $niceNumber")
            }
        }
    }

}