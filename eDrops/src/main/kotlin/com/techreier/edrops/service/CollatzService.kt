package com.techreier.edrops.service

import org.springframework.stereotype.Service

@Service
class CollatzService {
    // Return number of iterations and the resulting collatz sequence
    fun collatz(seed: Long): CollatzResult {
        val sequence = StringBuilder()
        var value = seed
        var iterations = 0L
        do {
            iterations++
            if ((value % 2) == 0L) {
                value /= 2
            } else {
                value = value * 3 + 1
            }
            sequence.append('→')
            sequence.append(value)
        } while ((value != 1L))
        return CollatzResult(iterations, sequence.toString())
    }
}

data class CollatzResult(val iterations: Long, val result: String)

