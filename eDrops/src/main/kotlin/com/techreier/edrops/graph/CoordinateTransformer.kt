package com.techreier.edrops.graph

class CoordinateTransformer(
    private val xMin: Double,
    private val xMax: Double,
    private val yMin: Double,
    private val yMax: Double,
    val diagramArea: DiagramArea
) {
    constructor(input: GraphInput, diagramArea: DiagramArea) : this(
        xMin = input.xMin,
        xMax = input.xMax,
        yMin = input.yMin,
        yMax = input.yMax,
        diagramArea = diagramArea
    )

    fun mapX(xMath: Double): Double {
        val ratio = (xMath - xMin) / (xMax - xMin)
        return diagramArea.anchorX + (ratio * diagramArea.plotWidth)
    }

    fun mapY(yMath: Double): Double {
        val ratio = (yMath - yMin) / (yMax - yMin)
        return (diagramArea.anchorY + diagramArea.plotHeight) - (ratio * diagramArea.plotHeight)
    }
}