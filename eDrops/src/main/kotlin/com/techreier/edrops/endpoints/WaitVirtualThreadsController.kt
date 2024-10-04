package com.techreier.edrops.endpoints

import com.techreier.edrops.config.logger
import org.springframework.context.annotation.Profile
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
@Profile("h2")
class WaitVirtualThreadsController {

    @GetMapping("/waitvt/{seconds}/{number}")
    fun waiting(@PathVariable seconds: Long, @PathVariable number: Int): String {
        logger.info("$number Before sleep with virtual threads")
        Thread.sleep(seconds * 1000) // Block with Thread.sleep (safe with virtual threads)
        logger.info("$number After sleep with virtual threads")
        return "OK virtual threads number: $number"
    }
}