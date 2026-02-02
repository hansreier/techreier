package com.techreier.edrops.endpoints

import com.techreier.edrops.config.PONG_TEXT
import com.techreier.edrops.config.logger
import org.springframework.context.annotation.Profile
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
@Profile("h2")
class PingController {

    @GetMapping("/ping")
    fun ping(
        @RequestParam(required = false) input: String?
    ): String {
        val text = input?.let { "$PONG_TEXT: $input" } ?: PONG_TEXT
        logger.info(text)
        return text
    }
}