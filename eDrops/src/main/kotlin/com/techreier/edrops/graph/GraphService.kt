package com.techreier.edrops.graph

import com.techreier.edrops.config.DEFAULT_STEPS
import com.techreier.edrops.config.MAX_STEPS
import com.techreier.edrops.config.MIN_STEPS
import org.springframework.stereotype.Service
import kotlin.math.max
import kotlin.math.min

@Service
class GraphService {

    fun generateSeries(
        limits: GraphLimits,
        mathFunction: (Double) -> Double,
        steps: Int = DEFAULT_STEPS
    ): DataSeries {
        val safeSteps = steps.coerceIn(MIN_STEPS, MAX_STEPS)
        val stepSize = (limits.xMax - limits.xMin) / safeSteps
        var yMin = Double.POSITIVE_INFINITY
        var yMax = Double.NEGATIVE_INFINITY
        var invalid = false

        val points = (0..steps).mapNotNull { i ->
            val xMath = limits.xMin + (i * stepSize)
            val yMath = mathFunction(xMath)

            if (yMath.isNaN() || yMath.isInfinite()) {
                invalid = true
                null
            } else {
                yMin = min(yMin, yMath)
                yMax = max(yMax, yMath)
                Point(x = xMath, y = yMath)
            }
        }

        return DataSeries(
            points = points,
            statistics = Statistics(limits.xMin, limits.xMax, yMin, yMax),
            invalid = invalid
        )
    }
}