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
        val blogParams = setCommonModelParameters(model, request)
        val doc: String = markdownToHtml(blogParams.docname)
        model.addAttribute("doc", doc)
        return "about"
    }

    @PostMapping
    fun getEntry(redirectAttributes: RedirectAttributes, docname: String): String {
        logger.info("docname: $docname")
        redirectAttributes.addFlashAttribute("docname", docname)
        return "redirect:/about"
    }
}