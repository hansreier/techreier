package com.techreier.edrops

import org.junit.jupiter.api.Test

const val MIN_SIZE = 1L
const val MAX_SIZE = 30L
const val DISPLAY_SEQUENCE = true

//Collatz Conjecture
//Collatz formodning
//https://www.quantamagazine.org/why-mathematicians-still-cant-solve-the-collatz-conjecture-20200922/
//divide by two if result is integer else multiply by 3 and add one. Repeat.
//You're out if the number is 1 or more than 100
class TestCollatz {

    @Test
    fun testCollatz() {
        for (start in MIN_SIZE..MAX_SIZE) {
            var max = start
            val sequence = StringBuilder()
            var value = start
            var iterations = 0
            do {
                iterations++
                if ((value % 2) == 0L) {
                    value /= 2
                } else {
                    value = value * 3 + 1
                    if (value > max) max = value
                }
          if (DISPLAY_SEQUENCE) {
              sequence.append('→')
              sequence.append(value)
          }
        } while ((value != 1L))
            println("$start X $iterations max: $max ${sequence}")
        }
    }
}
