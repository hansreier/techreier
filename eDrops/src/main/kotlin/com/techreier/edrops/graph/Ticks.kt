package com.techreier.edrops.graph

import com.techreier.edrops.config.MAX_SUBTICS
import com.techreier.edrops.config.MIN_SUBTICS
import com.techreier.edrops.service.FractionService
import org.slf4j.LoggerFactory
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow

private val logger = LoggerFactory.getLogger("com.techreier.edrops.util")
const val TOLERANCE = 1e-2

fun niceNumber(min: Double, max: Double, noSegments: Int):GraphSection {
    val fractionService = FractionService()
    val delta = (max - min) / noSegments
    val log10 = floor(log10(delta) + TOLERANCE).toLong()
    val scale = 10.0.pow(log10.toDouble())
    val seed = delta / scale
    logger.debug("delta=$delta scale=$scale seed=$seed")
    val fractionResult = fractionService.fraction(
        decimalNumber = seed,
        maxDeviation = 0.5,
        maxDenominator = 15,
        maxIterations = 4)
    logger.info("fractionResult={}", fractionResult)
    val value = (fractionResult.numerator.toDouble() / fractionResult.denominator.toDouble()) * scale
    val noSubSections =noSubSections(fractionResult.numerator)
    val subStep = value / noSubSections
    val realMin = floor((min + TOLERANCE) / subStep) * subStep
    return GraphSection(value, noSubSections, realMin)
}

fun noSubSections(numerator: Long):Int {
    var noSubTics = abs(numerator)
    if (noSubTics.equals(0L)) return MIN_SUBTICS
    while (noSubTics < MIN_SUBTICS) {
        noSubTics *= 2
    }
    return noSubTics.coerceAtMost(MAX_SUBTICS.toLong()).toInt()
}


fun axisData(min: Double, max: Double, noSegments: Int): AxisData {
    val graphSection = niceNumber(min, max, noSegments)
    val delta = graphSection.value
    val min = floor(min / delta + TOLERANCE) * delta
    val max = ceil(max / delta + TOLERANCE) * delta
    val no = ((max - min) / delta + TOLERANCE).toInt()
    return AxisData(delta, min, max, no, graphSection.noSubTics, graphSection.realMin)
}

data class AxisData(val delta: Double, val min: Double, val max: Double, val noTics: Int, val noSubTics: Int, val realMin: Double)

data class GraphSection(val value: Double, val noSubTics: Int, val realMin: Double)
