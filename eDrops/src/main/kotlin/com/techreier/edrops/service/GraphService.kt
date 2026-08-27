package com.techreier.edrops.service

import org.springframework.stereotype.Service

@Service
class GraphService {

    companion object {
        const val MAX_DEVIATION = 1e-6 //TODO ReierAsk use object if required.
    }


    fun svgChart(graphInput: GraphInput): SvgChart {
        // Faste dimensjoner for selve lerretet
        val chartWidth = 800.0
        val chartHeight = 500.0

        // Rammen for selve plottområdet (gir plass til akselabler på sidene)
        val plotX = 70.0
        val plotY = 40.0
        val plotWidth = 700.0
        val plotHeight = 400.0

        val plotArea = PlotArea(
            x = plotX,
            y = plotY,
            width = plotWidth,
            height = plotHeight
        )

        val xAxisY = plotY + plotHeight // Búnnlinjen for X-aksen

        // Hardkodet X-akse (BOTTOM) med min, midt- og maks-verdier fra input
        val xAxis = Axis(
            position = AxisPosition.BOTTOM,
            mainLine = LineSegment(
                x1 = plotX,
                y1 = xAxisY,
                x2 = plotX + plotWidth,
                y2 = xAxisY
            ),
            ticks = listOf(
                AxisTick(positionPx = plotX, label = String.format("%.1f", graphInput.xMin)),
                AxisTick(
                    positionPx = plotX + (plotWidth / 2),
                    label = String.format("%.1f", (graphInput.xMin + graphInput.xMax) / 2)
                ),
                AxisTick(positionPx = plotX + plotWidth, label = String.format("%.1f", graphInput.xMax))
            ),
            title = "X-akse"
        )

        // Hardkodet Y-akse (LEFT)
        val yAxis = Axis(
            position = AxisPosition.LEFT,
            mainLine = LineSegment(
                x1 = plotX,
                y1 = xAxisY,
                x2 = plotX,
                y2 = plotY
            ),
            ticks = listOf(
                AxisTick(positionPx = xAxisY, label = String.format("%.1f", graphInput.yMin)),
                AxisTick(
                    positionPx = plotY + (plotHeight / 2),
                    label = String.format("%.1f", (graphInput.yMin + graphInput.yMax) / 2)
                ),
                AxisTick(positionPx = plotY, label = String.format("%.1f", graphInput.yMax))
            ),
            title = "Y-akse"
        )

        return SvgChart(
            width = chartWidth,
            height = chartHeight,
            plotArea = plotArea,
            axes = listOf(xAxis, yAxis)
        )
    }
}


data class GraphInput(val xMin: Double, val xMax: Double, val yMin: Double, val yMax: Double)

enum class AxisPosition {
    LEFT, RIGHT, TOP, BOTTOM
}


data class Layout(val plotArea: PlotArea, val xAxis: Axis, val yAxis: Axis)

data class LineSegment(
    val x1: Double,
    val y1: Double,
    val x2: Double,
    val y2: Double
)

data class AxisTick(
    val positionPx: Double, // Posisjon langs aksen i piksler
    val label: String       // Ferdig formatert tekst (f.eks. "100")
)

data class Axis(
    val position: AxisPosition,
    val mainLine: LineSegment,
    val ticks: List<AxisTick> = emptyList(),
    val title: String? = null
)

data class PlotArea(
    val x: Double,
    val y: Double,
    val width: Double,
    val height: Double
)

data class SvgChart(
    val width: Double,      // Total width SVG-area
    val height: Double,     // Total height SVG-area
    val plotArea: PlotArea, // The plot area (for ramme)
    val axes: List<Axis> = emptyList()
)


