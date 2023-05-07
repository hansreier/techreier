package com.sigmondsmart.edrops.controllers

import com.sigmondsmart.edrops.config.logger
import com.sigmondsmart.edrops.domain.Docs
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
        val doc: String = markdownToHtml(Docs.getDoc(blogParams.blogId).name)
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