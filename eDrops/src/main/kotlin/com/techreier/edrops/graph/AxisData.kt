package com.techreier.edrops.graph

import com.techreier.edrops.config.MIN_HEIGHT_RATIO_FOR_SUBTICS
import com.techreier.edrops.config.TOLERANCE
import org.slf4j.LoggerFactory
import kotlin.math.*

private val logger = LoggerFactory.getLogger("com.techreier.edrops.util")

fun axisData(min: Double, max: Double, noSegments: Int, heightRatio: Double,  xAxis: Boolean): AxisData {

    if (max <= min) throw IllegalArgumentException("Max must be greater than min in axis calculation")

    val delta = (max - min) / noSegments
    val log10 = floor(log10(delta) + TOLERANCE).toLong()
    val scale = 10.0.pow(log10.toDouble())
    val seed = delta / scale
    logger.info("delta=$delta scale=$scale seed=$seed")
    val niceRange = niceNumber(seed)
    logger.info("niceRange={}", niceRange)
    val tickStep = niceRange.result * scale
    var subSectionsPerSection = niceRange.subTics
    val subTickStep = tickStep / subSectionsPerSection
    val subTickMin = correctToStep(min, subTickStep, true)
    val subTickMax = correctToStep(max, subTickStep, false)
    val subSectionCount = ((subTickMax - subTickMin) / subTickStep + TOLERANCE).toInt()
    val tickMin = correctToStep(subTickMin, tickStep, false)
    val tickMax = correctToStep(subTickMax, tickStep, true)
    if ((!xAxis ) && (heightRatio < MIN_HEIGHT_RATIO_FOR_SUBTICS)) subSectionsPerSection = 0
    val sectionCount = ((tickMax - tickMin) / tickStep + TOLERANCE).toInt()
    return AxisData(
        sectionCount, tickStep, tickMin, tickMax,
        subSectionsPerSection, subSectionCount, subTickStep, subTickMin, subTickMax
    )
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
