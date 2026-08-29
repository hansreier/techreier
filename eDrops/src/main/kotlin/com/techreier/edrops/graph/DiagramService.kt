package com.techreier.edrops.graph

import org.springframework.stereotype.Service

@Service
class DiagramService {

    companion object {
        const val TICK_LENGTH = 6.0
        const val X_LABEL_OFFSET = 22.0
        const val X_TITLE_OFFSET = 38.0
        const val Y_LABEL_OFFSET = 10.0
        const val Y_TITLE_OFFSET = 45.0
    }

    fun buildDiagram(input: GraphInput, plotArea: PlotArea, transformer: CoordinateTransformer): Diagram {
        val xAxis = createXAxis(input, plotArea, transformer)
        val yAxis = createYAxis(input, plotArea, transformer)

        return Diagram(
            width = 800.0,
            height = 500.0,
            plotArea = plotArea,
            axes = listOf(xAxis, yAxis)
        )
    }

    fun renderPolylines(dataSeries: List<DataSeries>, transformer: CoordinateTransformer): List<String> {
        return dataSeries.map { series ->
            series.points.joinToString(" ") { p ->
                val px = transformer.mapX(p.x)
                val py = transformer.mapY(p.y)
                "${px},${py}"
            }
        }
    }

    private fun createXAxis(input: GraphInput, plotArea: PlotArea, transformer: CoordinateTransformer): Axis {
        val yMath = if (0.0 in input.yMin..input.yMax) 0.0 else input.yMin
        val yPx = transformer.mapY(yMath)
        val xMinPx = transformer.mapX(input.xMin)
        val xMaxPx = transformer.mapX(input.xMax)
        val xMidPx = transformer.mapX((input.xMin + input.xMax) / 2)

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

    private fun createYAxis(input: GraphInput, plotArea: PlotArea, transformer: CoordinateTransformer): Axis {
        val xMath = if (0.0 in input.xMin..input.xMax) 0.0 else input.xMin
        val xPx = transformer.mapX(xMath)
        val yMinPx = transformer.mapY(input.yMin)
        val yMaxPx = transformer.mapY(input.yMax)
        val yMidPx = transformer.mapY((input.yMin + input.yMax) / 2)

        val yValues = listOf(
            input.yMin to yMinPx,
            (input.yMin + input.yMax) / 2 to yMidPx,
            input.yMax to yMaxPx
        )

        val ticks = yValues.map { (value, yPx) ->
            AxisTick(
                tickLine = LineSegment(x1 = xPx, y1 = yPx, x2 = xPx - TICK_LENGTH, y2 = yPx),
                labelPoint = Point(x = xPx - Y_LABEL_OFFSET, y = yPx + 4.0),
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
}