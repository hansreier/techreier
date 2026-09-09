package com.techreier.edrops.graph

import com.techreier.edrops.config.TOLERANCE
import com.techreier.edrops.config.logger
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import kotlin.math.abs
import kotlin.math.roundToInt

class AxisDataTest {

    @ParameterizedTest
    @MethodSource("axisData")
    fun axisDataTest(data: AxisTestData) {
        logger.info("min=${data.min}, max=${data.max} noTics=${data.sectionCount}")
        val axis = axisData(data.min, data.max, data.sectionCount)
        logger.info("calc axis: $axis")
        assertThat(axis.subTickMin).isLessThanOrEqualTo(data.min)
        assertThat(axis.subTickMax).isGreaterThanOrEqualTo(data.max)
        assertThat(axis.tickMax).isLessThanOrEqualTo(axis.subTickMax)
        assertThat(axis.tickMin).isGreaterThanOrEqualTo(axis.subTickMin)

        assertThat(axis.sectionCount + 1).isGreaterThanOrEqualTo(data.sectionCount)

        assertEquals(
            axis.subSectionCount,
            (abs(axis.subTickMax - axis.subTickMin) / axis.subTickStep).roundToInt()
        )
        assertEquals(
            axis.sectionCount,
            ((axis.tickMax - axis.tickMin) / axis.tickStep).roundToInt()
        )
        val ratio = axis.tickStep / axis.subTickStep
        assertEquals(
            ratio.roundToInt().toDouble(), ratio, TOLERANCE,
            "tickStep ($axis.tickStep) må være et eksakt multiplum av subTickStep ($axis.subTickStep)"
        )
    }

    companion object {

        @JvmStatic
        fun axisData(): List<AxisTestData> {
            return listOf(
                AxisTestData(min = -2.3, max = 15.3, sectionCount = 5),
                AxisTestData(min = 0.00000012, max = 0.00000089, sectionCount = 4),
                AxisTestData(min = 1_000_000_000.0, max = 8_500_000_000.0, sectionCount = 6),
                AxisTestData(min = -450.0, max = -12.5, sectionCount = 5),
                AxisTestData(min = -0.05, max = 0.05, sectionCount = 10)
            )
        }
    }

    data class AxisTestData(
        val min: Double,
        val max: Double,
        val sectionCount: Int,
    )
}