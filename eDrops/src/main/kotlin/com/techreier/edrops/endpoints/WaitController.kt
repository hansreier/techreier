package com.techreier.edrops.endpoints

import com.techreier.edrops.config.logger
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class WaitController {
    @GetMapping("/wait/{seconds}/{number}")
    fun waiting(@PathVariable seconds: Long, @PathVariable number: Int): String {
        runBlocking {
            pause(seconds, number)
        }
        logger.info("$number ${Thread.currentThread()}")
        return "OK $number"
    }
    suspend fun pause(seconds: Long, number: Int) {
        logger.info("Before delay $number")
        delay(seconds * 1000)
        logger.info("After delay $number")
    }
}