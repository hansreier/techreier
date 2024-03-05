package com.techreier.edrops

import org.junit.jupiter.api.Test

const val MIN_SIZE = 1L
const val MAX_SIZE = 30L
const val DISPLAY_SEQUENCE = true

//Collatz Conjecture
//Collatx formodning
//https://www.quantamagazine.org/why-mathematicians-still-cant-solve-the-collatz-conjecture-20200922/
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
