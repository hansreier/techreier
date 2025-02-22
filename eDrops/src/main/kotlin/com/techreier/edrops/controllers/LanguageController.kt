package com.techreier.edrops.controllers

import com.techreier.edrops.config.logger
import com.techreier.edrops.util.addFlashAttributes
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
        topicKey: String,
        path: String,
    ): String {
        logger.info("POST /language, and redirect")
        logger.debug("Language selected: $languageCode path: $path blogId: $blogId")
        redirectAttributes.addFlashAttributes(topicKey, blogId, languageCode)
        logger.debug("before redirect to get")
        return "redirect:$path?lang=$languageCode"
    }
}
