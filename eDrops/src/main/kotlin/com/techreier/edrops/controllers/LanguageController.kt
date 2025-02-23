package com.techreier.edrops.controllers

import com.techreier.edrops.config.logger
import jakarta.servlet.http.HttpSession
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping
class LanguageController {
    @PostMapping("/language")
    fun getLanguage(
        httpSession: HttpSession,
        languageCode: String?,
        blogId: Long?
    ): String {
        logger.info("POST /language, and redirect")
        val path = (httpSession.getAttribute("path") as String?) ?:"/$HOME_DIR"
        logger.debug("Language selected: {} path: {} blogId: {}", languageCode, path, blogId)
        logger.debug("before redirect to get")
        return "redirect:$path?lang=$languageCode"
    }
}
