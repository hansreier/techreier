package com.techreier.edrops.service

import com.techreier.edrops.config.logger
import com.techreier.edrops.util.float
import org.springframework.stereotype.Service
import kotlin.math.abs
import kotlin.math.floor

@Service
class GraphService {

    companion object {
        const val MAX_DEVIATION = 1e-6
        const val MAX_DENOMINATOR = Long.MAX_VALUE
        const val MAX_ITERATIONS = 500
        const val MAX_VIEW_ITERATIONS = 50
    }

    fun graph(graphInput: GraphInput): GraphResult {
        return graph(graphInput.decimalNumber, graphInput.maxDeviation, graphInput.maxDenominator)
    }

    /* Approximates a decimal number as a rational fraction using continued fraction expansion
    *
    * Each fraction is computed from the previous two as:
    * next numerator = floor(value) * previous numerator + numerator before that
    * next denominator = floor(value) * previous denominator + denominator before that
    * where value is inverted fractional remainder each step
    *
    * The stop criteria is max deviation and max denominator. The max number of iterations will in practice not be exceeded.
    */
    fun graph(
        decimalNumber: Double, maxDeviation: Double = MAX_DEVIATION, maxDenominator: Long = MAX_DENOMINATOR,
        maxIterations: Int = MAX_ITERATIONS,
    ): GraphResult {

        var numerator = 1L
        var denominator = 0L
        var numeratorOld = 0L
        var denominatorOld = 1L
        var i = 0
        var error: String? = null
        var value = decimalNumber
        val graphs: MutableList<Graph> = mutableListOf()
        logger.info("value $decimalNumber maxDeviation: $maxDeviation maxDenominator: $maxDenominator ")

        try {
            do {
                val integerPart = floor(value).toLong()

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
                val deviation = deviation(decimalNumber, numeratorNew, denominatorNew)
                numeratorOld = numerator
                denominatorOld = denominator
                numerator = numeratorNew
                denominator = denominatorNew
                logger.debug("numerator: $numeratorNew denominator: $denominatorNew value $value deviation $deviation")
                if (i <= MAX_VIEW_ITERATIONS) {
                    graphs.add(Graph(numerator, denominator, deviation.float()))
                } else {
                    error = "error.sequenceTruncated"
                    break
                }
                if (deviation < maxDeviation) {
                    break
                }

                value = 1.0 / (value - integerPart)

            } while (true)
        } catch (e: ArithmeticException) {
            logger.error("AritmeticException ${e.message}")
            error = "error.arithmetic"
        }
        return GraphResult(
            numerator, denominator, deviation(decimalNumber, numerator, denominator).float(), i, graphs, error
        )
    }

    private fun deviation(decimalNumber: Double, numerator: Long, denominator: Long) =
        abs(decimalNumber - numerator.toDouble() / denominator)
}

data class GraphInput(val decimalNumber: Double, val maxDeviation: Double, val maxDenominator: Long)

data class GraphResult(
    val numerator: Long, val denominator: Long, val deviation: String, val iterations: Int,
    val graphs: MutableList<Graph>, val error: String?,
)

data class Graph(
    val numerator: Long, val denominator: Long, val deviation: String,
)


