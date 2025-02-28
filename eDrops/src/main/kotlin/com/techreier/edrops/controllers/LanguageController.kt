package com.techreier.edrops.controllers

import com.techreier.edrops.config.logger
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
@RequestMapping
class LanguageController {
    @PostMapping("/language")
    fun getLanguage(
        redirectAttributes: RedirectAttributes,
        languageCode: String?,
        blogId: Long?,
        path: String,
    ): String {
        logger.info("POST /language, and redirect")
        redirectAttributes.addFlashAttribute("blogId", blogId)
        logger.debug("Language selected: {} path: {} blogId: {}", languageCode, path, blogId)
        logger.debug("before redirect to get")
        return "redirect:$path?lang=$languageCode"
    }
}
