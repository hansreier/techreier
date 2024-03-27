package com.techreier.edrops.service

import com.techreier.edrops.config.logger
import org.springframework.stereotype.Service
import kotlin.Long.Companion.MAX_VALUE

const val MAX_ITERATIONS = 5000L
const val MAX_VIEW_ITERATIONS = 1000L
const val MAX_ITEM_VALUE = (MAX_VALUE / 3) - 1

@Service
class CollatzService {
    // Return number of iterations and the resulting collatz sequence
    fun collatz(seed: Long): CollatzResult {
        val sequence = StringBuilder()
        sequence.append(seed)
        var value = seed
        var iterations = 0L
        var error : String? = null
        do {
            if (iterations >= MAX_ITERATIONS) {
                error = "error.maxIterations"
                break
            }
            if ((value % 2) == 0L) {
                value /= 2
            } else {
                if (value > MAX_ITEM_VALUE ) {
                    error = "error.maxValue"
                    break
                }
                value = value * 3 + 1
            }
            if (iterations <= MAX_VIEW_ITERATIONS) {
                sequence.append(" → ")
                sequence.append(value)
            } else error = "error.sequenceTruncated"
            iterations++
        } while ((value != 1L))
        logger.info("Seed: $seed Iterations: $iterations ${error?: ""}")
        return CollatzResult(iterations, sequence.toString(), error)
    }
}

data class CollatzResult(val iterations: Long, val sequence: String, val error: String?)

