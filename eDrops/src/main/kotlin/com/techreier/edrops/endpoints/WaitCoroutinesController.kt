package com.techreier.edrops.endpoints

import com.techreier.edrops.config.logger
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.springframework.context.annotation.Profile
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
@Profile("NotFoundProfile")
class WaitCoroutinesController {

    @GetMapping("/waitcr/{seconds}/{number}")
    fun waiting(@PathVariable seconds: Long, @PathVariable number: Int): String {
        runBlocking {
            pause(seconds, number)
        }
        logger.info("Current thread with coroutines: $number ${Thread.currentThread()}")
        return "OK coroutines number: $number"
    }

    suspend fun pause(seconds: Long, number: Int) {
        logger.info("Before delay with coroutines: $number")
        delay(seconds * 1000)
        logger.info("After delay with coroutines: $number")
    }
}