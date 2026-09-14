package com.techreier.edrops.graph

class CoordinateTransformer(
    private val xMin: Double,
    private val xMax: Double,
    private val yMin: Double,
    private val yMax: Double,
    val diagramArea: DiagramArea
) {
    constructor(limits: GraphLimits, diagramArea: DiagramArea) : this(
        xMin = limits.xMin,
        xMax = limits.xMax,
        yMin = limits.yMin,
        yMax = limits.yMax,
        diagramArea = diagramArea
    )

    fun mapX(xMath: Double): Double {
        val ratio = (xMath - xMin) / (xMax - xMin)
        return (diagramArea.plotAnchorX  + ratio * diagramArea.plotWidth)
    }

    fun mapY(yMath: Double): Double {
        val ratio = (yMath - yMin) / (yMax - yMin)
        return (diagramArea.plotAnchorY + diagramArea.plotHeight) - (ratio * diagramArea.plotHeight)
    }
}