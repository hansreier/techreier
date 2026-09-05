package com.techreier.edrops.graph


import com.techreier.edrops.config.*
import com.techreier.edrops.util.axis
import org.springframework.stereotype.Service

@Service
class DiagramService {

    companion object {
        const val TICK_LENGTH = 8.0
        const val SUBTICK_LENGTH = 5.0
        const val X_LABEL_OFFSET = 22.0
        const val Y_LABEL_OFFSET = 10.0
    }

    fun buildDiagram(input: GraphInput, plotArea: PlotArea): DiagramResult {

        val xSegments =
            ((plotArea.width / XSEGMENT_PIXELS) + TOLERANCE).toInt().coerceIn(XSEGMENTS_MIN, XSEGMENTS_MAX)
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
            xSubSegments = xAxisData.noSubTics,
            xMin =xAxisData.min,
            xMax = xAxisData.max,
            y = yAxisData.realMin,
            transformer = transformer)

        val yAxis = createYAxis(
            ySegments = yAxisData.noTics,
            ySubSegments = yAxisData.noSubTics,
            yMin = yAxisData.min,
            yMax = yAxisData.max,
            x = xAxisData.realMin,
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
        xSegments: Int, xSubSegments: Int, xMin: Double, xMax: Double, y: Double, transformer: CoordinateTransformer,
    ): Axis {

        val xMinPx = transformer.mapX(xMin)
        val xMaxPx = transformer.mapX(xMax)
        val yPx = transformer.mapY(y)

        val xStep = (xMax - xMin) / xSegments
        val xSubStep = xStep / xSubSegments
        logger.info("xStep: $xStep")

        val ticks = List(xSegments + 1) { i ->
            val xValue = xMin + (i * xStep)
            val xPx = transformer.mapX(xValue)

            // Generer SubTicks kun for segmentet mellom dette hovedsteget og det neste
            val subTics = if (i < xSegments && xSubSegments > 1) {
                (1 until xSubSegments).map { j ->
                    val subXValue = xValue + (j * xSubStep)
                    val subXPx = transformer.mapX(subXValue)
                    val subYPx = yPx + SUBTICK_LENGTH

                    SubTick(
                        tickLine = LineSegment(x1 = subXPx, y1 = yPx, x2 = subXPx, y2 = subYPx),
                        subTicPoint = Point(x = subXPx, y = subYPx)
                    )
                }
            } else {
                emptyList()
            }

            AxisTick(
                tickLine = LineSegment(x1 = xPx, y1 = yPx, x2 = xPx, y2 = yPx + TICK_LENGTH),
                labelPoint = Point(x = xPx, y = yPx + X_LABEL_OFFSET),
                label = xValue.axis(),
                textAlignment = TextAlignment.CENTER,
                subTics = subTics
            )
        }

        // 3. Slå sammen hoved-ticks og sub-ticks
        return Axis(
            position = AxisPosition.BOTTOM,
            mainLine = LineSegment(x1 = xMinPx, y1 = yPx, x2 = xMaxPx, y2 = yPx),
            ticks = ticks
        )
    }

    private fun createYAxis(
        ySegments: Int,
        ySubSegments: Int,
        yMin: Double,
        yMax: Double,
        x: Double,
        transformer: CoordinateTransformer
    ): Axis {
        val safeYSegments = ySegments.coerceAtLeast(1)
        val safeYSubSegments = ySubSegments.coerceAtLeast(1)

        val yMinPx = transformer.mapY(yMin)
        val yMaxPx = transformer.mapY(yMax)
        val xPx = transformer.mapX(x)

        val yStep = (yMax - yMin) / safeYSegments
        val ySubStep = yStep / safeYSubSegments
        val subTickLength = TICK_LENGTH * 0.5

        logger.info("yStep: $yStep")

        val ticks = List(safeYSegments + 1) { i ->
            val yValue = yMin + (i * yStep)
            val yPx = transformer.mapY(yValue)

            // Generer SubTicks kun for segmentet mellom dette hovedsteget og det neste
            val subTics = if (i < safeYSegments && safeYSubSegments > 1) {
                (1 until safeYSubSegments).map { j ->
                    val subYValue = yValue + (j * ySubStep)
                    val subYPx = transformer.mapY(subYValue)
                    val subXPx = xPx - subTickLength

                    SubTick(
                        tickLine = LineSegment(x1 = xPx, y1 = subYPx, x2 = subXPx, y2 = subYPx),
                        subTicPoint = Point(x = subXPx, y = subYPx)
                    )
                }
            } else {
                emptyList()
            }

            AxisTick(
                tickLine = LineSegment(x1 = xPx, y1 = yPx, x2 = xPx - TICK_LENGTH, y2 = yPx),
                labelPoint = Point(x = xPx - Y_LABEL_OFFSET, y = yPx + 4.0),
                label = yValue.axis(),
                textAlignment = TextAlignment.END,
                subTics = subTics
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