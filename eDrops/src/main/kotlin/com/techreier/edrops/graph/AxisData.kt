package com.techreier.edrops.graph

import com.techreier.edrops.config.MAX_SUBTICS
import com.techreier.edrops.config.MIN_SUBTICS
import com.techreier.edrops.service.FractionService
import org.slf4j.LoggerFactory
import kotlin.math.*

private val logger = LoggerFactory.getLogger("com.techreier.edrops.util")
const val TOLERANCE = 1e-5

fun axisData(min: Double, max: Double, noSegments: Int): AxisData {

    if (max <= min) throw IllegalArgumentException("Max must be greater than min in axis calculation")

    val fractionService = FractionService()
    val delta = (max - min) / noSegments
    val log10 = floor(log10(delta) + TOLERANCE).toLong()
    val scale = 10.0.pow(log10.toDouble())
    val seed = delta / scale
    logger.info("delta=$delta scale=$scale seed=$seed")
    val fractionResult = fractionService.fraction(
        decimalNumber = seed,
        maxDeviation = 0.4,
        maxDenominator = 10,
        maxIterations = 4
    )
    logger.info("fractionResult={}", fractionResult)
    val tickStep = (fractionResult.numerator.toDouble() / fractionResult.denominator.toDouble()) * scale
    val subSectionsPerSection = subSectionCount(fractionResult.numerator)
    val subTickStep = tickStep / subSectionsPerSection
    val subTickMin = correctToStep(min, subTickStep, true)
    val subTickMax = correctToStep(max, subTickStep, false)
    val subSectionCount = ((subTickMax - subTickMin) / subTickStep + TOLERANCE).toInt()
    val tickMin = correctToStep(subTickMin, tickStep, false)
    val tickMax = correctToStep(subTickMax, tickStep, true)
    val sectionCount = ((tickMax - tickMin) / tickStep + TOLERANCE).toInt()
    return AxisData(
        sectionCount, tickStep, tickMin, tickMax,
        subSectionsPerSection, subSectionCount, subTickStep, subTickMin, subTickMax
    )
}

fun subSectionCount(numerator: Long): Int {
    var subSectionCount = abs(numerator)
    if (subSectionCount == 0L) return MIN_SUBTICS
    while (subSectionCount < MIN_SUBTICS) {
        subSectionCount *= 2
    }
    return subSectionCount.coerceAtMost(MAX_SUBTICS.toLong()).toInt()
}

fun correctToStep(value: Double, step: Double, down: Boolean): Double {
    return if (down) {
        val corr = value + step * TOLERANCE
        corr - corr.mod(step)
    } else {
        val corr = value - step * TOLERANCE
        corr - corr.mod(step) + step
    }
}

data class AxisData(
    val sectionCount: Int,
    val tickStep: Double,
    val tickMin: Double,
    val tickMax: Double,
    val subSectionsPerSection: Int,
    val subSectionCount: Int,
    val subTickStep: Double,
    val subTickMin: Double,
    val subTickMax: Double,
)
