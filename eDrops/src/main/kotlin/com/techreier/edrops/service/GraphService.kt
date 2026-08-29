package com.techreier.edrops.service

import org.springframework.stereotype.Service
import kotlin.math.max
import kotlin.math.min

@Service
class GraphService {

    fun generateSeries(
        input: GraphInput,
        mathFunction: (Double) -> Double,
        steps: Int = 200
    ): DataSeries {
        val stepSize = (input.xMax - input.xMin) / steps
        var yMin = Double.POSITIVE_INFINITY
        var yMax = Double.NEGATIVE_INFINITY

        val points = (0..steps).mapNotNull { i ->
            val xMath = input.xMin + (i * stepSize)
            val yMath = mathFunction(xMath)

            if (yMath.isNaN() || yMath.isInfinite()) {
                null
            } else {
                yMin = min(yMin, yMath)
                yMax = max(yMax, yMath)
                Point(x = xMath, y = yMath) // Kun matematiske koordinater
            }
        }

        return DataSeries(
            points = points,
            statistics = Statistics(input.xMin, input.xMax, yMin, yMax)
        )
    }
}


