package com.techreier.edrops.controllers

import com.techreier.edrops.config.logger
import com.techreier.edrops.service.DbService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes

const val ADMIN="/admin"

@Controller
@RequestMapping
class AdminController(private val dbService: DbService): BaseController(dbService)
{
    @GetMapping("$ADMIN/{tag}")
    fun allBlogEntries(@PathVariable tag: String?, @RequestParam(required = false, name = "lang") langCode: String? ,
                       request: HttpServletRequest, model: Model): String {
        val blogParams = setCommonModelParameters(model, request, langCode, tag)
        logger.info("allBlogEntries Fetch blog entries with: $blogParams")
        val blog = dbService.readBlogWithSameLanguage(blogParams.blogId, blogParams.langCode )
        model.addAttribute("blog", blog)
        logger.info("getting GUI with blogEntries")
        return "blogEntries"
    }

    @PostMapping(ADMIN)
    fun getBlogAdmin(redirectAttributes: RedirectAttributes, result: String): String {
        return redirect(redirectAttributes, result, ADMIN)
    }

}