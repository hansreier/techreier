package com.techreier.edrops.graph

import org.springframework.stereotype.Service
import kotlin.math.max
import kotlin.math.min

@Service
class GraphService {

    fun generateSeries(
        limits: GraphLimits,
        mathFunction: (Double) -> Double,
        steps: Int = 20000
    ): DataSeries {
        val stepSize = (limits.xMax - limits.xMin) / steps
        var yMin = Double.POSITIVE_INFINITY
        var yMax = Double.NEGATIVE_INFINITY

        val points = (0..steps).mapNotNull { i ->
            val xMath = limits.xMin + (i * stepSize)
            val yMath = mathFunction(xMath)

            if (yMath.isNaN() || yMath.isInfinite()) {
                null
            } else {
                yMin = min(yMin, yMath)
                yMax = max(yMax, yMath)
                Point(x = xMath, y = yMath)
            }
        }

        return DataSeries(
            points = points,
            statistics = Statistics(limits.xMin, limits.xMax, yMin, yMax)
        )
    }
}