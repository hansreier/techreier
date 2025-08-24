package com.techreier.edrops.service

import com.techreier.edrops.config.logger
import org.springframework.stereotype.Service
import kotlin.math.abs

@Service
class FractionService {

    fun fraction(fractionInput: FractionInput): FractionResult {
        return fraction(fractionInput.decimalNumber, fractionInput.epsilon, fractionInput.maxDenominator)
    }

    /* Approximates a decimal number as a rational fraction using continued fraction expansion
    *
    * Each fraction is computed from the previous two as:
    * next numerator = floor(value) * previous numerator + numerator before that
    * next denominator = floor(value) * previous denominator + denominator before that
    * where value is inverted fractional remainder each step
    *
    * The stop criteria is accuracy and max denominator. The max number of iterations will in practice not be exceeded.
    */
    fun fraction(decimalNumber: Double, epsilon: Double = 1e-6, maxDenominator: Long = Long.MAX_VALUE,
                 maxIterations: Int = MAX_ITERATIONS): FractionResult {

        var numerator = 1L
        var denominator = 0L
        var numeratorOld = 0L
        var denominatorOld = 1L
        var i = 0
        var error : String? = null
        var value = decimalNumber
        val sequence = StringBuilder()
        logger.info("value $decimalNumber epsilon: $epsilon maxDenominator: $maxDenominator ")

        try {
            do {
                val integerPart = kotlin.math.floor(value).toLong()

                val numeratorNew = Math.addExact(
                    Math.multiplyExact(integerPart, numerator), numeratorOld
                )
                val denominatorNew = Math.addExact(
                    Math.multiplyExact(integerPart, denominator), denominatorOld
                )
                i++
                if (i >= maxIterations) {
                    error = "error.maxIterations"
                    break
                }
                if (denominatorNew > maxDenominator) {
                    error = "error.maxDenominator"
                    break
                }
                val accuracy = accuracy(decimalNumber, numeratorNew, denominatorNew)
                numeratorOld = numerator
                denominatorOld = denominator
                numerator = numeratorNew
                denominator = denominatorNew
                logger.info("numerator: $numeratorNew denominator: $denominatorNew value $value accuracy $accuracy")
                if (i <= MAX_VIEW_ITERATIONS) {
                    if (i > 1) sequence.append(" →")
                    sequence.append("(${numerator}/${denominator}-${String.format("%.5g", accuracy)})")
                } else {
                    error = "error.sequenceTruncated"
                    break
                }
                if (accuracy < epsilon) {
                    break
                }

                value = 1.0 / (value - integerPart)

            } while (true)
        } catch (e: ArithmeticException) {
            logger.error("AritmeticException ${e.message}")
            error = "error.arithmetic"
        }
        return FractionResult(numerator, denominator, accuracy(decimalNumber, numerator, denominator), i,
            sequence.toString(), error)
    }
}

private fun accuracy(decimalNumber: Double, numerator: Long, denominator: Long) =
    abs(decimalNumber - numerator.toDouble() / denominator)


data class FractionInput(val decimalNumber: Double, val epsilon: Double, val maxDenominator: Long)

data class FractionResult(
    val numerator: Long, val denominator: Long, val accuracy: Double, val iterations: Int, val sequence: String,
    val error: String?
)


