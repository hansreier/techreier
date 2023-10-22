package com.techreier.edrops.controllers

import com.techreier.edrops.config.logger
import com.techreier.edrops.service.DbService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes

const val ADMIN="admin"
const val ADMIN_DIR= "/$ADMIN"

@Controller
@RequestMapping(ADMIN_DIR)
class AdminController(private val dbService: DbService): BaseController(dbService)
{
    @GetMapping("/{tag}")
    fun allBlogEntries(@PathVariable tag: String?, @RequestParam(required = false, name = "lang") langCode: String? ,
                       request: HttpServletRequest, model: Model): String {
        val blogParams = setCommonModelParameters(model, request, langCode, tag)
        if (blogParams.blogId <0) { //tag is not found, redirect to default page with same language
            return "redirect:$ADMIN_DIR/${fetchFirstBlog(blogParams.langCode).tag}"
        }
        logger.info("allBlogEntries Fetch blog entries with: $blogParams")
        val blog = dbService.readBlogWithSameLanguage(blogParams.blogId, blogParams.langCode )
        model.addAttribute("blog", blog)
        logger.info("getting GUI with blogEntries")
        return "blogEntries"
    }

    @GetMapping
    fun redirect(@RequestParam(required = false, name = "lang") language: String?,
                 request: HttpServletRequest, model: Model): String {
        val blogParams = setCommonModelParameters(model, request, language)
        return "redirect:$ADMIN_DIR/${fetchFirstBlog(blogParams.langCode).tag}"
    }

    @PostMapping
    fun getBlogAdmin(redirectAttributes: RedirectAttributes, result: String): String {
        return redirect(redirectAttributes, result, ADMIN_DIR)
    }

}