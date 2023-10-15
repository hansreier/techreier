package com.techreier.edrops.controllers

import com.techreier.edrops.config.logger
import com.techreier.edrops.service.DbService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes

const val BLOG="/blogs"

@Controller
@RequestMapping
class BlogController(private val dbService: DbService): BaseController(dbService)
{
    @GetMapping("$BLOG/{tag}")
    fun allBlogTexts(@PathVariable tag: String?, @RequestParam(required = false, name = "lang") langCode: String? ,
                     request: HttpServletRequest, model: Model): String {
        val blogParams = setCommonModelParameters(model, request, langCode, tag)
        logger.info("allBlogEntries Fetch blog entries with: $blogParams and summary")
        val blog = dbService.readBlogWithSameLanguage(blogParams.blogId, blogParams.langCode )
        model.addAttribute("blog", blog)
        return "blogSummaries"
    }
    @PostMapping(BLOG)
    fun getBlog(redirectAttributes: RedirectAttributes, result: String): String {
        return redirect(redirectAttributes, result, BLOG)
    }
}