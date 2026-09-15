package com.techreier.edrops.graph


import com.techreier.edrops.config.*
import com.techreier.edrops.config.logger
import com.techreier.edrops.util.axis
import org.springframework.stereotype.Service
import java.lang.Integer.max
import kotlin.math.abs
import kotlin.math.pow

@Service
class DiagramService {

    fun buildDiagram(limits: GraphLimits, diagramArea: DiagramArea, metaData: GraphMetadata): DiagramResult {
        if (limits.xMax <= limits.xMin) throw IllegalArgumentException("xMax must be greater than xMin")
        if (limits.yMax <= limits.yMin) throw IllegalArgumentException("yMax must be greater than yMin")

        // Calculate y-axis first, x-axis parameters need to be corrected based on it
        val ySegments =
            ((diagramArea.plotHeight / YSEGMENT_PIXELS) + 1 + TOLERANCE).toInt().coerceIn(YSEGMENTS_MIN, YSEGMENTS_MAX)
        val yAxisData = axisData(limits.yMin, limits.yMax, ySegments, metaData.heightRatio, false)
        logger.debug("y: wantedSegments: {} yAxisData: {}", ySegments, yAxisData)

        var yValue = yAxisData.tickMin
        var maxDigits = 0
        for (i in 1..yAxisData.sectionCount) { // Calculate the label with largest number of digits
            val label = yValue.axis(minThreshold = yAxisData.subTickStep / 10)
            maxDigits = max(label.length, maxDigits)
            yValue += yAxisData.tickStep
        }

        val fontScale = metaData.fontScale * metaData.heightRatio.pow(FONT_SCALE_EXPONENT)
        val charWidth = fontScale * X_FONT_FACTOR
        val plotCorrX = (maxDigits * charWidth)
        diagramArea.plotWidth -= plotCorrX
        diagramArea.plotAnchorX += plotCorrX
        logger.debug("xFontFactor=$X_FONT_FACTOR maxDigits: $maxDigits fontScale=$fontScale, charWidth=$charWidth plotCorrX=${plotCorrX}")

        // Calculate x-axis
        val xSegments =
            ((diagramArea.plotWidth / XSEGMENT_PIXELS) + TOLERANCE).toInt().coerceIn(XSEGMENTS_MIN, XSEGMENTS_MAX)
        val xAxisData = axisData(limits.xMin, limits.xMax, xSegments, metaData.heightRatio, true)
        logger.debug("x: wantedSegments: {} xAxisData: {}", xSegments, xAxisData)
        val limits = GraphLimits(
            xMin = xAxisData.subTickMin,
            xMax = xAxisData.subTickMax,
            yMin = yAxisData.subTickMin,
            yMax = yAxisData.subTickMax
        )
        // transform to screen coordinates
        val transformer = CoordinateTransformer(limits = limits, diagramArea = diagramArea)

        // create the axes
        val xAxis = createXAxis(
            xAxisData, yAxisData.subTickMin, yAxisData.subTickMax,
            transformer = transformer, fontScale
        )
        val yAxis = createYAxis(yAxisData, xAxisData.subTickMin, xMax = xAxisData.subTickMax, transformer = transformer)

        val diagram = Diagram(
            area = diagramArea,
            fontScale = fontScale,
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
        axisData: AxisData,
        yMin: Double, yMax: Double,
        transformer: CoordinateTransformer,
        fontScale: Double,
    ): Axis {
        val yMinPx = transformer.mapY(yMin)
        val yMaxPx = transformer.mapY(yMax)
        val ticks = List(axisData.sectionCount + 1) { i ->
            val xValue = axisData.tickMin + (i * axisData.tickStep)
            val xPx = transformer.mapX(xValue)
            AxisTick(
                tickLine = LineSegment(x1 = xPx, y1 = yMinPx, x2 = xPx, y2 = yMinPx + TICK_LENGTH),
                labelPoint = Point(x = xPx, y = yMinPx + X_LABEL_OFFSET + fontScale),
                label = xValue.axis(minThreshold = axisData.subTickStep / 10),
                textAlignment = TextAlignment.CENTER
            )
        }

        val subTicks = if (axisData.subSectionsPerSection > 0) {
            (0..axisData.subSectionCount)
                .map { i -> axisData.subTickMin + (i * axisData.subTickStep) }
                .filter { subXValue ->
                    abs(subXValue.rem(axisData.tickStep)) > TOLERANCE
                }.map { subXValue ->
                    val subXPx = transformer.mapX(subXValue)
                    AxisTick(
                        tickLine = LineSegment(x1 = subXPx, y1 = yMinPx, x2 = subXPx, y2 = yMinPx + SUBTICK_LENGTH),
                        labelPoint = null,
                        label = null,
                        textAlignment = TextAlignment.CENTER
                    )
                }
        } else listOf()
        val xMinPx = transformer.mapX(axisData.subTickMin)
        val xMaxPx = transformer.mapX(axisData.subTickMax)

        val gridLines = ticks
            .filter { tick ->
                tick.tickLine.x1 > (xMinPx + TOLERANCE) && tick.tickLine.x1 < (xMaxPx - TOLERANCE)
            }
            .map { tick ->
                LineSegment(
                    x1 = tick.tickLine.x1,
                    y1 = yMinPx,
                    x2 = tick.tickLine.x1,
                    y2 = yMaxPx
                )
            }

        com.techreier.edrops.graph.logger.info("No of gridlines x: ${gridLines.size}")

        return Axis(
            position = AxisPosition.BOTTOM,
            mainLine = LineSegment(
                x1 = transformer.mapX(axisData.subTickMin),
                y1 = yMinPx,
                x2 = transformer.mapX(axisData.subTickMax),
                y2 = yMinPx
            ),
            ticks = ticks,
            subTicks = subTicks,
            gridLines = gridLines
        )
    }
}

private fun createYAxis(
    axisData: AxisData,
    xMin: Double,
    xMax: Double,
    transformer: CoordinateTransformer,
): Axis {
    val xMinPx = transformer.mapX(xMin)
    val xMaxPx = transformer.mapX(xMax)

    val ticks = List(axisData.sectionCount + 1) { i ->
        val yValue = axisData.tickMin + (i * axisData.tickStep)
        val yPx = transformer.mapY(yValue)

        AxisTick(
            tickLine = LineSegment(x1 = xMinPx, y1 = yPx, x2 = xMinPx - TICK_LENGTH, y2 = yPx),
            labelPoint = Point(x = xMinPx - Y_LABEL_OFFSET, y = yPx + 4.0),
            label = yValue.axis(minThreshold = axisData.subTickStep / 10),
            textAlignment = TextAlignment.END
        )
    }

    val subTicks = if (axisData.subSectionsPerSection > 0) {
        (0..axisData.subSectionCount)
            .map { i -> axisData.subTickMin + (i * axisData.subTickStep) }
            .filter { subYValue ->
                abs((subYValue.rem(axisData.tickStep))) > TOLERANCE
            }.map { subYValue ->
                logger.info("subYValue: $subYValue")
                val subYPx = transformer.mapY(subYValue)
                AxisTick(
                    tickLine = LineSegment(x1 = xMinPx, y1 = subYPx, x2 = xMinPx - SUBTICK_LENGTH, y2 = subYPx),
                    labelPoint = null,
                    label = null,
                    textAlignment = TextAlignment.END
                )
            }
    } else listOf()

    val yMinPx = transformer.mapY(axisData.subTickMin)
    val yMaxPx = transformer.mapY(axisData.subTickMax)

    val gridLines = ticks
        .filter { tick ->
            tick.tickLine.y1 > (yMaxPx + TOLERANCE) && tick.tickLine.y1 < (yMinPx - TOLERANCE)
        }
        .map { tick ->
            LineSegment(
                x1 = xMinPx,
                y1 = tick.tickLine.y1,
                x2 = xMaxPx,
                y2 = tick.tickLine.y2,
            )
        }

    logger.info("No of gridlines y: ${gridLines.size}")

    return Axis(
        position = AxisPosition.LEFT,
        mainLine = LineSegment(x1 = xMinPx, y1 = yMinPx, x2 = xMinPx, y2 = yMaxPx),
        ticks = ticks,
        subTicks = subTicks,
        gridLines = gridLines
    )
}

data class DiagramResult(
    val diagram: Diagram,
    val transformer: CoordinateTransformer,
)
