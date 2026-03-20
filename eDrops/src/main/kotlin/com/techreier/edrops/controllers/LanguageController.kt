package com.techreier.edrops.controllers

import com.techreier.edrops.config.logger
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping
class LanguageController {
    @PostMapping("/language")
    fun getLanguage(
        languageCode: String?,
        path: String,
    ): String {
        logger.info("POST /language, and redirect")
        logger.debug("Language selected: {} path: {}", languageCode, path)
        logger.debug("before redirect to get")
        return "redirect:$path?lang=$languageCode"
    }
}
