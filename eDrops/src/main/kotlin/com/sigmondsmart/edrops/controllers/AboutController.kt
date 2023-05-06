package com.sigmondsmart.edrops.controllers

import com.sigmondsmart.edrops.config.logger
import com.sigmondsmart.edrops.service.DbService
import com.sigmondsmart.edrops.util.markdownToHtml
import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
@RequestMapping("/about")
class AboutController(dbService: DbService): BaseController(dbService)
{
    @GetMapping
    fun content(request: HttpServletRequest, model: Model): String {
        logger.info("content")
        val readme = markdownToHtml("readme.md")
        setCommonModelParameters(model, request)
        model.addAttribute("content", readme)
        return "about"
    }

    @PostMapping
    fun getEntry(redirectAttributes: RedirectAttributes): String {
        logger.info("readme valgt")
       // redirectAttributes.addFlashAttribute("blogid", blog)
        return "redirect:/about"
    }
}