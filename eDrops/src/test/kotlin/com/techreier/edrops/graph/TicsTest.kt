package com.techreier.edrops.graph

import com.techreier.edrops.config.logger
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.byLessThan
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class TicsTest {

    @Test
    fun axisDataTest() {
        val min = -2.3
        val max = 15.3
        val noTics = 5
        val delta = (max - min) / noTics
        val initialAxis = AxisData(delta, min, max, noTics)
        logger.info("init axis: $initialAxis")
        val axis = axisData(min = -2.3, max = 15.3, noSegments = 5)
        logger.info("calc axis: $axis")
        assertThat(axis.min).isLessThanOrEqualTo(min)
        assertThat(axis.max).isGreaterThanOrEqualTo(max)
        assertThat(axis.delta).isGreaterThanOrEqualTo(delta)
        assertEquals(noTics, axis.noTics)
        assertThat((axis.max - axis.min) / axis.noTics)
            .isCloseTo(axis.delta, byLessThan(TOLERANCE))
    }

    // Manual inspection is best for veriFying nice numbers used on grapth axis
    @Disabled
    @Test
    fun testNiceNumbersSweep() {
        var interval = 10.0
        repeat(20) {
            logger.info("--- interval = $interval ---")
            for (noSegments in 3..10) {
                val niceNumber = niceNumber(0.0, interval, noSegments)
                logger.info("noTics=$noSegments -> niceNumber = $niceNumber")
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
            for (noSegments in 3..10) {
                val niceNumber = niceNumber(0.0, interval, noSegments)
                logger.info("tricky interval=$interval, noTics=$noSegments -> niceNumber = $niceNumber")
            }
        }
    }

}