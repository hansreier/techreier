package com.techreier.edrops.graph

import com.techreier.edrops.config.TOLERANCE
import com.techreier.edrops.config.logger
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

class CorrectToStepTest {

    @ParameterizedTest
    @MethodSource("correctToStepData")
    fun correctToStepTest(data: CorrectToStepTestData) {
        val niceValue = correctToStep(data.value, data.step, data.down)
        logger.info("value=${data.value} step=${data.step} down=${data.down} niceValue=$niceValue")
        assertEquals(data.expected, niceValue, TOLERANCE)
    }

    companion object {
        @JvmStatic
        fun correctToStepData(): List<CorrectToStepTestData> {
            return listOf(
                // --- Runder ned (down = true) ---
                CorrectToStepTestData(value = -2.33, step = 0.5, down = true, expected = -2.5),
                CorrectToStepTestData(value = -2.000000001, step = 0.5, down = true, expected = -2.0),
                CorrectToStepTestData(value = -2.01, step = 0.5, down = true, expected = -2.5),
                CorrectToStepTestData(value = 15.3, step = 5.0, down = true, expected = 15.0),
                CorrectToStepTestData(value = 0.0, step = 10.0, down = true, expected = 0.0),

                // --- Runder opp (down = false) ---
                CorrectToStepTestData(value = 15.3, step = 5.0, down = false, expected = 20.0),
                CorrectToStepTestData(value = 15.000000001, step = 5.0, down = false, expected = 15.0),
                CorrectToStepTestData(value = 15.01, step = 5.0, down = false, expected = 20.0),
                CorrectToStepTestData(value = -2.33, step = 0.5, down = false, expected = -2.0),
                CorrectToStepTestData(value = 0.0, step = 10.0, down = false, expected = 0.0),

                // --- Ekstreme skalaer (mikroskopisk / makroskopisk) ---
                CorrectToStepTestData(value = 0.00089, step = 0.0002, down = false, expected = 0.0010),
                CorrectToStepTestData(value = 0.00089, step = 0.0002, down = true, expected = 0.0008),
                CorrectToStepTestData(value = 1_234_567.0, step = 50_000.0, down = false, expected = 1_250_000.0),
                CorrectToStepTestData(value = 1_234_567.0, step = 50_000.0, down = true, expected = 1_200_000.0)
            )
        }
    }

    data class CorrectToStepTestData(
        val value: Double,
        val step: Double,
        val down: Boolean,
        val expected: Double
    )

}