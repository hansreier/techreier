package com.techreier.edrops.controllers

import com.techreier.edrops.config.logger
import com.techreier.edrops.service.DbService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes

const val BLOG="blogs"
const val BLOG_DIR= "/$BLOG"

@Controller
@RequestMapping(BLOG_DIR)
class BlogController(private val dbService: DbService): BaseController(dbService)
{
    @GetMapping("/{tag}")
    fun allBlogTexts(@PathVariable tag: String?, @RequestParam(required = false, name = "lang") langCode: String? ,
                     request: HttpServletRequest, model: Model): String {
        val blogParams = setCommonModelParameters(model, request, langCode, tag)
        if (blogParams.blogId <0) { //tag is not found, redirect to default page with same language
            return "redirect:$BLOG_DIR/${fetchFirstBlog(blogParams.langCode).tag}"
        }
        logger.info("allBlogEntries Fetch blog entries with: $blogParams and summary")
        val blog = dbService.readBlogWithSameLanguage(blogParams.blogId, blogParams.langCode )
        model.addAttribute("blog", blog)
        return "blogSummaries"
    }

    @GetMapping
    fun redirect(@RequestParam(required = false, name = "lang") language: String?,
                 request: HttpServletRequest, model: Model): String {
        val blogParams = setCommonModelParameters(model, request, language)
        return "redirect:$BLOG_DIR/${fetchFirstBlog(blogParams.langCode).tag}"
    }
    @PostMapping
    fun getBlog(redirectAttributes: RedirectAttributes, result: String): String {
        return redirect(redirectAttributes, result, BLOG_DIR)
    }
}