package com.techreier.edrops.service

data class GraphInput(
    val xMin: Double,
    val xMax: Double,
    val yMin: Double,
    val yMax: Double
)

enum class AxisPosition {
    LEFT, RIGHT, TOP, BOTTOM
}

enum class TextAlignment {
    START, CENTER, END
}

data class Point(
    val x: Double,
    val y: Double
)

data class LineSegment(
    val x1: Double,
    val y1: Double,
    val x2: Double,
    val y2: Double
)

data class Statistics (
    val xMin: Double = Double.NEGATIVE_INFINITY,
    val xMax : Double = Double.POSITIVE_INFINITY,
    val yMin: Double = Double.NEGATIVE_INFINITY,
    val yMax: Double = Double.POSITIVE_INFINITY,
)

data class DataSeries(
    val id: String? = null,
    val label: String? = null,
    val points: List<Point> = emptyList(),
    val error: String? = null,
    val statistics: Statistics
)

data class AxisTick(
    val tickLine: LineSegment, // Eksakte piksel-koordinater for selve tick-streken
    val labelPoint: Point,     // Eksakt (x, y) hvor teksten skal plasseres
    val label: String,
    val textAlignment: TextAlignment
)

data class AxisTitle(
    val point: Point,
    val text: String,
    val rotationDegrees: Double = 0.0,
    val textAlignment: TextAlignment = TextAlignment.CENTER
)

data class Axis(
    val position: AxisPosition,
    val mainLine: LineSegment,
    val ticks: List<AxisTick> = emptyList(),
    val title: AxisTitle? = null
)

data class PlotArea(
    val x: Double,
    val y: Double,
    val width: Double,
    val height: Double
)

data class Diagram(
    val width: Double,
    val height: Double,
    val plotArea: PlotArea,
    val axes: List<Axis> = emptyList(),
    val error: String? = null,
)

data class Graph(
    val diagram: Diagram,
    val dataSeries: List<DataSeries> = emptyList(),
)