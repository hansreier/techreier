package com.techreier.edrops.controllers

import com.techreier.edrops.config.logger
import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
@RequestMapping
class LanguageController {

    @PostMapping("/language")
    fun getLanguage(
        request: HttpServletRequest, redirectAttributes: RedirectAttributes, languageCode: String?,
        blogId: Long?, path: String
    ): String {
        logger.info("POST /language, and redirect")
        logger.debug("Language selected: $languageCode path: $path blogId: $blogId")
        redirectAttributes.addFlashAttribute("languageCode", languageCode)
        //Or else id blogId used to populate menu will not be preserved
        //Alternative to use hidden field is either cookie or store in session
        //If more state is needed, using Spring session (and store session in db) is recommended.
        redirectAttributes.addFlashAttribute("blogId", blogId)
        logger.debug("before redirect to get")
        return "redirect:$path?lang=$languageCode"
    }
}