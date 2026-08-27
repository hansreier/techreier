package com.techreier.edrops.service

import org.springframework.stereotype.Service

@Service
class GraphService {

    companion object {
        const val MAX_DEVIATION = 1e-6 //TODO ReierAsk use object if required.
    }


    fun svgGraph(graphInput: GraphInput): SvgGraph {
        val chartWidth = 800.0
        val chartHeight = 500.0

        val plotArea = PlotArea(
            x = 70.0,
            y = 40.0,
            width = 700.0,
            height = 400.0
        )

        // X-akse plassering
        val xAxisYMath = if (0.0 in graphInput.yMin..graphInput.yMax) 0.0 else graphInput.yMin
        val xAxisPx = mapY(xAxisYMath, graphInput, plotArea)

        val xAxis = Axis(
            position = AxisPosition.BOTTOM,
            mainLine = LineSegment(
                x1 = mapX(graphInput.xMin, graphInput, plotArea),
                y1 = xAxisPx,
                x2 = mapX(graphInput.xMax, graphInput, plotArea),
                y2 = xAxisPx
            ),
            ticks = listOf(
                AxisTick(positionPx = mapX(graphInput.xMin, graphInput, plotArea), label = String.format("%.1f", graphInput.xMin)),
                AxisTick(positionPx = mapX((graphInput.xMin + graphInput.xMax) / 2, graphInput, plotArea), label = String.format("%.1f", (graphInput.xMin + graphInput.xMax) / 2)),
                AxisTick(positionPx = mapX(graphInput.xMax, graphInput, plotArea), label = String.format("%.1f", graphInput.xMax))
            ),
            title = "X-akse"
        )

        // Y-akse plassering
        val yAxisXMath = if (0.0 in graphInput.xMin..graphInput.xMax) 0.0 else graphInput.xMin
        val yAxisPx = mapX(yAxisXMath, graphInput, plotArea)

        val yAxis = Axis(
            position = AxisPosition.LEFT,
            mainLine = LineSegment(
                x1 = yAxisPx,
                y1 = mapY(graphInput.yMin, graphInput, plotArea),
                x2 = yAxisPx,
                y2 = mapY(graphInput.yMax, graphInput, plotArea)
            ),
            ticks = listOf(
                AxisTick(positionPx = mapY(graphInput.yMin, graphInput, plotArea), label = String.format("%.1f", graphInput.yMin)),
                AxisTick(positionPx = mapY((graphInput.yMin + graphInput.yMax) / 2, graphInput, plotArea), label = String.format("%.1f", (graphInput.yMin + graphInput.yMax) / 2)),
                AxisTick(positionPx = mapY(graphInput.yMax, graphInput, plotArea), label = String.format("%.1f", graphInput.yMax))
            ),
            title = "Y-akse"
        )

        return SvgGraph(
            width = chartWidth,
            height = chartHeight,
            plotArea = plotArea,
            axes = listOf(xAxis, yAxis)
        )
    }

    private fun mapX(x: Double, graphInput: GraphInput, plotArea: PlotArea): Double {
        val ratio = (x - graphInput.xMin) / (graphInput.xMax - graphInput.xMin)
        return plotArea.x + (ratio * plotArea.width)
    }

    private fun mapY(y: Double, graphInput: GraphInput, plotArea: PlotArea): Double {
        val ratio = (y - graphInput.yMin) / (graphInput.yMax - graphInput.yMin)
        return (plotArea.y + plotArea.height) - (ratio * plotArea.height)
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

data class SvgGraph(
    val width: Double,      // Total width SVG-area
    val height: Double,     // Total height SVG-area
    val plotArea: PlotArea, // The plot area (for ramme)
    val axes: List<Axis> = emptyList(),
    val error: String? = null
)


