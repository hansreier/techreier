package com.techreier.edrops.graph


import com.techreier.edrops.config.*
import com.techreier.edrops.util.axis
import org.springframework.stereotype.Service

@Service
class DiagramService {

    companion object {
        const val TICK_LENGTH = 6.0
        const val X_LABEL_OFFSET = 22.0
        const val Y_LABEL_OFFSET = 10.0
    }

    fun buildDiagram(input: GraphInput, plotArea: PlotArea): DiagramResult {

        val xSegments = ((plotArea.width / XSEGMENT_PIXELS) + TOLERANCE).toInt().coerceIn(XSEGMENTS_MIN, XSEGMENTS_MAX)
        val ySegments =
            ((plotArea.height / YSEGMENT_PIXELS) + 1 + TOLERANCE).toInt().coerceIn(YSEGMENTS_MIN, YSEGMENTS_MAX)
        val xAxisData = axisData(input.xMin, input.xMax, xSegments)
        logger.info("x: wantedSegments: $xSegments xAxisData: $xAxisData")
        val yAxisData = axisData(input.yMin, input.yMax, ySegments)
        logger.info("y: wantedSegments: $ySegments yAxisData: $yAxisData")
        val input = GraphInput(xAxisData.min, xAxisData.max, yAxisData.min, yAxisData.max)
        val transformer = CoordinateTransformer(input = input, plotArea = plotArea)

        val xAxis = createXAxis(
            xSegments = xAxisData.noTics,
            xMin =xAxisData.min,
            xMax = xAxisData.max,
            y = yAxisData.min,
            transformer = transformer)

        val yAxis = createYAxis(
            ySegments = yAxisData.noTics,
            yMin = yAxisData.min,
            yMax = yAxisData.max,
            x = xAxisData.min,
            transformer = transformer)

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

    private fun createXAxis(
        xSegments: Int, xMin: Double, xMax: Double, y: Double, transformer: CoordinateTransformer,
    ): Axis {

        val xMinPx = transformer.mapX(xMin)
        val xMaxPx = transformer.mapX(xMax)
        val yPx = transformer.mapY(y)
        val xStep = (xMax - xMin) / (xSegments)
        logger.info("xStep: $xStep")
        val xValues: List<Pair<Double, Double>> = List(xSegments + 1) { i ->
            val xValue = xMin + (i * xStep)
            xValue to transformer.mapX(xValue)
        }

        val ticks = xValues.map { (value, xPx) ->
            AxisTick(
                tickLine = LineSegment(x1 = xPx, y1 = yPx, x2 = xPx, y2 = yPx + TICK_LENGTH),
                labelPoint = Point(x = xPx, y = yPx + X_LABEL_OFFSET),
                label = value.axis(),
                textAlignment = TextAlignment.CENTER
            )
        }


        return Axis(
            position = AxisPosition.BOTTOM,
            mainLine = LineSegment(x1 = xMinPx, y1 = yPx, x2 = xMaxPx, y2 = yPx),
            ticks = ticks
        )
    }


    private fun createYAxis(
        ySegments: Int, yMin: Double, yMax: Double, x: Double, transformer: CoordinateTransformer,
    ): Axis {


        val yMinPx = transformer.mapY(yMin)
        val yMaxPx = transformer.mapY(yMax)
        val xPx = transformer.mapX(x)
        val yStep = (yMax - yMin) / (ySegments)
        logger.info("yStep: $yStep")
        val yValues: List<Pair<Double, Double>> = List(ySegments + 1) { i ->
            val yValue = yMin + (i * yStep)
            yValue to transformer.mapY(yValue)
        }

        val ticks = yValues.map { (value, yPx) ->
            AxisTick(
                tickLine = LineSegment(x1 = xPx, y1 = yPx, x2 = xPx - TICK_LENGTH, y2 = yPx),
                labelPoint = Point(x = xPx - Y_LABEL_OFFSET, y = yPx + 4.0),
                label = value.axis(),
                textAlignment = TextAlignment.END
            )
        }

        return Axis(
            position = AxisPosition.LEFT,
            mainLine = LineSegment(x1 = xPx, y1 = yMinPx, x2 = xPx, y2 = yMaxPx),
            ticks = ticks
        )
    }

    data class DiagramResult(
        val diagram: Diagram,
        val transformer: CoordinateTransformer,
    )
}