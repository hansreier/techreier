package com.techreier.edrops

import com.techreier.edrops.config.logger
import org.junit.jupiter.api.Test

const val MIN_SIZE = 1L
const val MAX_SIZE = 30L

//Collatz Conjecture
//Collatx formodning
//https://www.quantamagazine.org/why-mathematicians-still-cant-solve-the-collatz-conjecture-20200922/
class TestCollatz {

    @Test
    fun testCollatz() {
         val displaySequence = true
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
          if (displaySequence) {
              sequence.append('→')
              sequence.append(value)
          }
        } while ((value != 1L))
            logger.info("$start X $iterations ${sequence}")
        }
    }
}
