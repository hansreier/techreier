package com.techreier.edrops.graph

import com.techreier.edrops.config.XTICS_MAX
import com.techreier.edrops.config.XTICS_MIN
import com.techreier.edrops.config.XTIC_PIXELS
import com.techreier.edrops.config.YTICS_MAX
import com.techreier.edrops.config.YTICS_MIN
import com.techreier.edrops.config.YTIC_PIXELS

import com.techreier.edrops.config.logger
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

    fun buildDiagram(input: GraphInput, plotArea: PlotArea): DiagramResult {

        val xTics = ((plotArea.width / XTIC_PIXELS) + 1 + TOLERANCE ).toInt().coerceIn(XTICS_MIN, XTICS_MAX)
        val yTics =((plotArea.height / YTIC_PIXELS) + 1 + TOLERANCE ).toInt().coerceIn(YTICS_MIN, YTICS_MAX)
        val xAxisData = axisData(input.xMin, input.xMax, xTics)
        logger.info("x: wantedTics: $xTics xAxisData: $xAxisData")
        val yAxisData = axisData(input.yMin, input.yMax, yTics)
        logger.info("y: wantedTics: $yTics yAxisData: $yAxisData")
        val input = GraphInput(xAxisData.min, xAxisData.max, yAxisData.min, yAxisData.max)
        val transformer = CoordinateTransformer(input = input, plotArea = plotArea)

        val xAxis = createXAxis(xAxisData.noTics, input, plotArea, transformer)
        val yAxis = createYAxis(yAxisData.noTics, input,  plotArea, transformer)

        val diagram = Diagram(
            width = 800.0,
            height = 500.0,
            plotArea = plotArea,
            axes = listOf(xAxis, yAxis)
        )

        return DiagramResult(diagram, transformer)

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

    private fun createXAxis(xTics: Int, input: GraphInput, plotArea: PlotArea, transformer: CoordinateTransformer): Axis {

        val xMinPx = transformer.mapX(input.xMin)
        val xMaxPx = transformer.mapX(input.xMax)
        val yPx = transformer.mapY(input.yMin)
        val xStep = (input.xMax - input.xMin) / (xTics - 1)
        logger.info("xStep: $xStep")
        val xValues: List<Pair<Double, Double>> = List(xTics) { i ->
            val xValue = input.xMin + (i * xStep)
            xValue to transformer.mapX(xValue)
        }

        val ticks = xValues.map { (value, xPx) ->
            AxisTick(
                tickLine = LineSegment(x1 = xPx, y1 = yPx, x2 = xPx, y2 = yPx + TICK_LENGTH),
                labelPoint = Point(x = xPx, y = yPx + X_LABEL_OFFSET),
                label = String.format("%.2f", value), //TODO ReierAsk format
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


    private fun createYAxis(yTics: Int, input: GraphInput, plotArea: PlotArea, transformer: CoordinateTransformer): Axis {


        val yMinPx = transformer.mapY(input.yMin)
        val yMaxPx = transformer.mapY(input.yMax)
        val xPx = transformer.mapX(input.xMin)
        val yStep = (input.yMax - input.yMin) / (yTics - 1)
        logger.info("yStep: $yStep")
        val yValues: List<Pair<Double, Double>> = List(yTics) { i ->
            val yValue = input.yMin + (i * yStep)
            yValue to transformer.mapY(yValue)
        }

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

    data class DiagramResult(
        val diagram: Diagram,
        val transformer: CoordinateTransformer
    )
}