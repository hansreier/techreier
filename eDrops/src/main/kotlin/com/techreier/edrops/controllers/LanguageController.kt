package com.techreier.edrops.controllers

import com.techreier.edrops.config.logger
import jakarta.servlet.http.HttpSession
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
@RequestMapping
class LanguageController {
    @PostMapping("/language")
    fun getLanguage(
        redirectAttributes: RedirectAttributes,
        httpSession: HttpSession,
       @RequestParam languageCode: String?,
       @RequestParam blogId: Long?
    ): String {
        logger.info("POST /language, and redirect")
        val path = (httpSession.getAttribute("path") as String?) ?:"/$HOME_DIR" //TODO change back to hidden field to avoid return to homepage
        redirectAttributes.addFlashAttribute("blogId", blogId)
        logger.debug("Language selected: {} path: {} blogId: {}", languageCode, path, blogId)
        logger.debug("before redirect to get")
        return "redirect:$path?lang=$languageCode"
    }
}
