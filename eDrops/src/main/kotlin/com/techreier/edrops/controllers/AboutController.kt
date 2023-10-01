package com.techreier.edrops.controllers

import com.techreier.edrops.config.logger
import com.techreier.edrops.domain.Docs
import com.techreier.edrops.service.DbService
import com.techreier.edrops.util.markdownToHtml
import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
@RequestMapping("/about")
class AboutController(dbService: DbService) : BaseController(dbService) {
    @GetMapping
    fun content(request: HttpServletRequest, model: Model): String {
        logger.info("content: ${request.servletPath}")
        val blogParams = setCommonModelParameters(model, request)
        val doc =  Docs.getDoc(blogParams.blogId)
        val docText: String = markdownToHtml(doc)
        model.addAttribute("docText", docText)
        model.addAttribute("doc", doc)
        return "about"
    }

    @PostMapping
    fun getEntry(redirectAttributes: RedirectAttributes, doc: Long): String {
        logger.info("docid: $doc")
        redirectAttributes.addFlashAttribute("blogid", doc)
        return "redirect:/about"
    }
}