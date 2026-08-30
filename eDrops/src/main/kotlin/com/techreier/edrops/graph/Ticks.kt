package com.techreier.edrops.graph

import com.techreier.edrops.service.FractionService
import org.slf4j.LoggerFactory
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow

private val logger = LoggerFactory.getLogger("com.techreier.edrops.util")
private const val TOLERANCE = 1e-2

fun niceNumber(min: Double, max: Double, noTics: Int):Double {
    val fractionService = FractionService()
    val delta = (max - min) / noTics
    val log10 = floor(log10(delta) + TOLERANCE).toLong()
    val scale = 10.0.pow(log10.toDouble())
    val seed = delta / scale
    logger.debug("delta=$delta scale=$scale seed=$seed")
    val fractionResult = fractionService.fraction(
        decimalNumber = seed,
        maxDeviation = 0.5,
        maxDenominator = 15,
        maxIterations = 4)
    logger.debug("fractionResult={}", fractionResult)
    val niceNumber = (fractionResult.numerator.toDouble() / fractionResult.denominator.toDouble()) * scale
    return niceNumber

}
