package com.techreier.edrops.service

import com.techreier.edrops.config.logger
import org.springframework.stereotype.Service
import kotlin.math.abs


@Service
class FractionService {
    // Approximates a decimal number as a rational fraction using continued fraction expansion
    fun fraction(value: Double, epsilon: Double = 1e-6, maxDenominator: Long = Long.MAX_VALUE): Fraction {

        var numerator = 1L
        var denominator = 0L
        var numeratorOld = 0L
        var denominatorOld = 1L
        var workingValue = value
        logger.info("value $value epsilon: $epsilon maxDenominator: $maxDenominator ")

        try {
            while (true) {
                val integerPart = kotlin.math.floor(workingValue).toLong()

                val numeratorNew = Math.addExact(
                    Math.multiplyExact(integerPart, numerator),
                    numeratorOld
                )
                val denominatorNew = Math.addExact(
                    Math.multiplyExact(integerPart, denominator),
                    denominatorOld
                )

                if (denominatorNew > maxDenominator) {
                    return Fraction(numerator, denominator, accuracy(value, numerator, denominator), true)
                }
                val accuracy = accuracy(value, numeratorNew, denominatorNew)
                logger.info("numerator (teller): $numeratorNew denominator (nevner): $denominatorNew value $workingValue accuracy $accuracy")
                if (accuracy < epsilon) {
                    return Fraction(numeratorNew, denominatorNew, accuracy)
                }

                numeratorOld = numerator
                denominatorOld = denominator
                numerator = numeratorNew
                denominator = denominatorNew
                workingValue = 1.0 / (workingValue - integerPart)
            }
        } catch (_: ArithmeticException) {
            return Fraction(numerator, denominator, accuracy(value, numerator, denominator), true)
        }
    }
}

private fun accuracy(decimalNumber: Double, numerator: Long, denominator: Long) =
    abs(decimalNumber - numerator.toDouble() / denominator)


data class Fraction(val numerator: Long, val denominator: Long, val accuracy: Double, val error: Boolean = false)


