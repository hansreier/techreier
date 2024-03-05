package com.techreier.edrops

import org.junit.jupiter.api.Test

const val MIN_SIZE = 1L
const val MAX_SIZE = 300L
const val DISPLAY_SEQUENCE = true

//Collatz Conjecture
//Collatz formodning
//https://www.quantamagazine.org/why-mathematicians-still-cant-solve-the-collatz-conjecture-20200922/
//divide by two if result is integer else multiply by 3 and add one. Repeat.
// 8 minutes. As many numbers as possible until you reach 1
// If your not down to one after 8 minutes you loose.
class TestCollatz {

    @Test
    fun testCollatz() {
        for (start in MIN_SIZE..MAX_SIZE) {
            val sequence = StringBuilder()
            var value = start
            var iterations = 0
            do {
                iterations++
                if ((value % 2) == 0L) {
                    value /= 2
                } else {
                    value = value * 3 + 1
                }
          if (DISPLAY_SEQUENCE) {
              sequence.append('→')
              sequence.append(value)
          }
        } while ((value != 1L))
            println("$start X $iterations ${sequence}")
        }
    }
}
