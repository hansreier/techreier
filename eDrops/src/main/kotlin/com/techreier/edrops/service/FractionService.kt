package com.techreier.edrops.service

import com.techreier.edrops.config.logger
import org.springframework.stereotype.Service
import kotlin.math.abs

@Service
class FractionService {

    fun fraction(fractionInput: FractionInput): Fraction {
        return fraction(fractionInput.decimalNumber, fractionInput.epsilon, fractionInput.maxDenominator)
    }

    // Approximates a decimal number as a rational fraction using continued fraction expansion
    fun fraction(decimalNumber: Double, epsilon: Double = 1e-6, maxDenominator: Long = Long.MAX_VALUE): Fraction {

        var numerator = 1L
        var denominator = 0L
        var numeratorOld = 0L
        var denominatorOld = 1L
        var iterations = 0
        var workingDecimalNumber = decimalNumber
        logger.info("value $decimalNumber epsilon: $epsilon maxDenominator: $maxDenominator ")

        try {
            while (iterations < MAX_ITERATIONS) {
                val integerPart = kotlin.math.floor(workingDecimalNumber).toLong()

                val numeratorNew = Math.addExact(
                    Math.multiplyExact(integerPart, numerator),
                    numeratorOld
                )
                val denominatorNew = Math.addExact(
                    Math.multiplyExact(integerPart, denominator),
                    denominatorOld
                )
                iterations++
                if (denominatorNew > maxDenominator) {
                    return Fraction(numerator, denominator, accuracy(decimalNumber, numerator, denominator), iterations, true)
                }
                val accuracy = accuracy(decimalNumber, numeratorNew, denominatorNew)
                logger.info("numerator (teller): $numeratorNew denominator (nevner): $denominatorNew value $workingDecimalNumber accuracy $accuracy")
                if (accuracy < epsilon) {
                    return Fraction(numeratorNew, denominatorNew,  accuracy, iterations)
                }

                numeratorOld = numerator
                denominatorOld = denominator
                numerator = numeratorNew
                denominator = denominatorNew
                workingDecimalNumber = 1.0 / (workingDecimalNumber - integerPart)
            }
            return Fraction(numerator, denominator, accuracy(decimalNumber, numerator, denominator), iterations, true)
        } catch (_: ArithmeticException) {
            return Fraction(numerator, denominator, accuracy(decimalNumber, numerator, denominator), iterations, true)
        }
    }
}

private fun accuracy(decimalNumber: Double, numerator: Long, denominator: Long) =
    abs(decimalNumber - numerator.toDouble() / denominator)


data class FractionInput(val decimalNumber: Double, val epsilon: Double, val maxDenominator: Long)

data class Fraction(val numerator: Long, val denominator: Long, val accuracy: Double, val iterations: Int,
                    val error: Boolean = false)


