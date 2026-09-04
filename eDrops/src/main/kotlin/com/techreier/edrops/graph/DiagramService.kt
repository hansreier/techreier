package com.techreier.edrops.graph

import com.techreier.edrops.config.XSEGMENTS_MAX
import com.techreier.edrops.config.XSEGMENTS_MIN
import com.techreier.edrops.config.XSEGMENT_PIXELS
import com.techreier.edrops.config.YSEGMENTS_MAX
import com.techreier.edrops.config.YSEGMENTS_MIN
import com.techreier.edrops.config.YSEGMENT_PIXELS


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

        val xSegments = ((plotArea.width / XSEGMENT_PIXELS) + TOLERANCE ).toInt().coerceIn(XSEGMENTS_MIN, XSEGMENTS_MAX)
        val ySegments =((plotArea.height / YSEGMENT_PIXELS) + 1 + TOLERANCE ).toInt().coerceIn(YSEGMENTS_MIN, YSEGMENTS_MAX)
        val xAxisData = axisData(input.xMin, input.xMax, xSegments)
        logger.info("x: wantedSegments: $xSegments xAxisData: $xAxisData")
        val yAxisData = axisData(input.yMin, input.yMax, ySegments)
        logger.info("y: wantedSegments: $ySegments yAxisData: $yAxisData")
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

    private fun createXAxis(xSegments: Int, input: GraphInput, plotArea: PlotArea, transformer: CoordinateTransformer): Axis {

        val xMinPx = transformer.mapX(input.xMin)
        val xMaxPx = transformer.mapX(input.xMax)
        val yPx = transformer.mapY(input.yMin)
        val xStep = (input.xMax - input.xMin) / (xSegments)
        logger.info("xStep: $xStep")
        val xValues: List<Pair<Double, Double>> = List(xSegments + 1) { i ->
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


    private fun createYAxis(ySegments: Int, input: GraphInput, plotArea: PlotArea, transformer: CoordinateTransformer): Axis {


        val yMinPx = transformer.mapY(input.yMin)
        val yMaxPx = transformer.mapY(input.yMax)
        val xPx = transformer.mapX(input.xMin)
        val yStep = (input.yMax - input.yMin) / (ySegments)
        logger.info("yStep: $yStep")
        val yValues: List<Pair<Double, Double>> = List(ySegments + 1) { i ->
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