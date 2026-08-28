package com.techreier.edrops.service

import org.springframework.stereotype.Service

@Service
class GraphService {

    companion object {
        const val TICK_LENGTH = 6.0
        const val X_LABEL_OFFSET = 22.0
        const val X_TITLE_OFFSET = 38.0
        const val Y_LABEL_OFFSET = 10.0
        const val Y_TITLE_OFFSET = 45.0
    }

    fun graph(graphInput: GraphInput): Graph {
        val chartWidth = 800.0
        val chartHeight = 500.0

        val plotArea = PlotArea(
            x = 70.0,
            y = 40.0,
            width = 700.0,
            height = 400.0
        )

        val xAxis = createXAxis(graphInput, plotArea)
        val yAxis = createYAxis(graphInput, plotArea)

        val diagram = Diagram(
            width = chartWidth,
            height = chartHeight,
            plotArea = plotArea,
            axes = listOf(xAxis, yAxis)
        )

        val sinusCurve = generateSeries(
            input = graphInput,
            plotArea = plotArea,
            mathFunction = { x -> kotlin.math.sin(x) }
        )

        return Graph(
           diagram, listOf(sinusCurve)
        )
    }

    private fun createXAxis(input: GraphInput, plotArea: PlotArea): Axis {
        val yMath = if (0.0 in input.yMin..input.yMax) 0.0 else input.yMin
        val yPx = mapY(yMath, input, plotArea)
        val xMinPx = mapX(input.xMin, input, plotArea)
        val xMaxPx = mapX(input.xMax, input, plotArea)
        val xMidPx = mapX((input.xMin + input.xMax) / 2, input, plotArea)

        val xValues = listOf(
            input.xMin to xMinPx,
            (input.xMin + input.xMax) / 2 to xMidPx,
            input.xMax to xMaxPx
        )

        val ticks = xValues.map { (value, xPx) ->
            AxisTick(
                tickLine = LineSegment(x1 = xPx, y1 = yPx, x2 = xPx, y2 = yPx + TICK_LENGTH),
                labelPoint = Point(x = xPx, y = yPx + X_LABEL_OFFSET),
                label = String.format("%.1f", value),
                textAlignment = TextAlignment.CENTER
            )
        }

        val title = AxisTitle(
            point = Point(x = plotArea.x + (plotArea.width / 2), y = yPx + X_TITLE_OFFSET),
            text = "X-akse",
            textAlignment = TextAlignment.CENTER
        )

        return Axis(
            position = AxisPosition.BOTTOM,
            mainLine = LineSegment(x1 = xMinPx, y1 = yPx, x2 = xMaxPx, y2 = yPx),
            ticks = ticks,
            title = title
        )
    }

    private fun createYAxis(input: GraphInput, plotArea: PlotArea): Axis {
        val xMath = if (0.0 in input.xMin..input.xMax) 0.0 else input.xMin
        val xPx = mapX(xMath, input, plotArea)
        val yMinPx = mapY(input.yMin, input, plotArea)
        val yMaxPx = mapY(input.yMax, input, plotArea)
        val yMidPx = mapY((input.yMin + input.yMax) / 2, input, plotArea)

        val yValues = listOf(
            input.yMin to yMinPx,
            (input.yMin + input.yMax) / 2 to yMidPx,
            input.yMax to yMaxPx
        )

        val ticks = yValues.map { (value, yPx) ->
            AxisTick(
                tickLine = LineSegment(x1 = xPx, y1 = yPx, x2 = xPx - TICK_LENGTH, y2 = yPx),
                labelPoint = Point(x = xPx - Y_LABEL_OFFSET, y = yPx + 4.0), // +4.0 for vertikal sentrering av font
                label = String.format("%.1f", value),
                textAlignment = TextAlignment.END
            )
        }

        val title = AxisTitle(
            point = Point(x = xPx - Y_TITLE_OFFSET, y = plotArea.y + (plotArea.height / 2)),
            text = "Y-akse",
            rotationDegrees = -90.0,
            textAlignment = TextAlignment.CENTER
        )

        return Axis(
            position = AxisPosition.LEFT,
            mainLine = LineSegment(x1 = xPx, y1 = yMinPx, x2 = xPx, y2 = yMaxPx),
            ticks = ticks,
            title = title
        )
    }

    private fun mapX(x: Double, input: GraphInput, plotArea: PlotArea): Double {
        val ratio = (x - input.xMin) / (input.xMax - input.xMin)
        return plotArea.x + (ratio * plotArea.width)
    }

    private fun mapY(y: Double, input: GraphInput, plotArea: PlotArea): Double {
        val ratio = (y - input.yMin) / (input.yMax - input.yMin)
        return (plotArea.y + plotArea.height) - (ratio * plotArea.height)
    }

    private fun generateSeries(
        input: GraphInput,
        plotArea: PlotArea,
        mathFunction: (Double) -> Double,
        steps: Int = 200
    ): DataSeries {
        val stepSize = (input.xMax - input.xMin) / steps

        val points = (0..steps).mapNotNull { i ->
            val xMath = input.xMin + (i * stepSize)
            val yMath = mathFunction(xMath)

            if (yMath.isNaN() || yMath.isInfinite()) {
                null
            } else {
                Point(
                    x = mapX(xMath, input, plotArea),
                    y = mapY(yMath, input, plotArea)
                )
            }
        }

        return DataSeries(points = points)
    }
}


