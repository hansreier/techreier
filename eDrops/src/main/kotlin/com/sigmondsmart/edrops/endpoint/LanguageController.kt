package com.sigmondsmart.edrops.endpoint

import com.sigmondsmart.edrops.config.logger
import com.sigmondsmart.edrops.service.DbService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
//@RequestMapping("/l")
class LanguageController(private val dbService: DbService) : BaseController(dbService) {

    @PostMapping("/language")
    fun getLanguage(
        request: HttpServletRequest, redirectAttributes: RedirectAttributes, code: String?,
        blogid: Long?, path: String
    ): String {
        logger.debug("Language selected: $code path: ${request.servletPath} blogid: $blogid")
        redirectAttributes.addFlashAttribute("langcode", code)
        //Or else id blogid used to populate menu will not be preserved
        //Alternative to use hidden field is either cookie or store in session
        //If more state is needed to use Spring session (and store session in db) is recommended.
        redirectAttributes.addFlashAttribute("blogid", blogid)
        logger.debug("before redirect to get")
        return "redirect:$path?lang=$code"
    }
}