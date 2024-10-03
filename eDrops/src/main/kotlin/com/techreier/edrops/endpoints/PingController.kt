package com.techreier.edrops.endpoints

import com.techreier.edrops.config.logger
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class PingController {

    @GetMapping("/ping")
    fun ping(): String {
        logger.info("pong from TechReier")
        return "pong from TechReier"
    }
}