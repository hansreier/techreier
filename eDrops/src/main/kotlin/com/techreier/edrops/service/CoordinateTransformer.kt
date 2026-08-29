package com.techreier.edrops.service

class CoordinateTransformer(
    private val xMin: Double,
    private val xMax: Double,
    private val yMin: Double,
    private val yMax: Double,
    val plotArea: PlotArea
) {
    constructor(input: GraphInput, plotArea: PlotArea) : this(
        xMin = input.xMin,
        xMax = input.xMax,
        yMin = input.yMin,
        yMax = input.yMax,
        plotArea = plotArea
    )

    fun mapX(xMath: Double): Double {
        val ratio = (xMath - xMin) / (xMax - xMin)
        return plotArea.x + (ratio * plotArea.width)
    }

    fun mapY(yMath: Double): Double {
        val ratio = (yMath - yMin) / (yMax - yMin)
        return (plotArea.y + plotArea.height) - (ratio * plotArea.height)
    }

    fun mapPoint(mathPoint: Point): Point {
        return Point(x = mapX(mathPoint.x), y = mapY(mathPoint.y))
    }
}