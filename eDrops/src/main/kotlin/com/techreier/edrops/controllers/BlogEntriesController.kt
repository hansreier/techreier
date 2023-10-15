package com.techreier.edrops.controllers

import com.techreier.edrops.config.logger
import com.techreier.edrops.service.DbService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
@RequestMapping
class BlogEntriesController(private val dbService: DbService): BaseController(dbService)
{

    @GetMapping("/blogs/{tag}")
    fun allBlogTexts(@PathVariable tag: String?, @RequestParam(required = false, name = "lang") langCode: String? ,
                     request: HttpServletRequest, model: Model): String {
        val blogParams = setCommonModelParameters(model, request, langCode, tag)
        logger.info("allBlogEntries Fetch blog entries with: $blogParams and summary")
        val blog = dbService.readBlogWithSameLanguage(blogParams.blogId, blogParams.langCode )
        model.addAttribute("blog", blog)
        return "blogSummaries"
    }

    @PostMapping("/blogs")
    fun getBlog(redirectAttributes: RedirectAttributes, result: String): String {
        //alternatives hidden input fields (process entire list and select by name) or server state, this is easier
        val tag = result.substringBefore(" ","")
        val blogId = result.substringAfter(" ","0").toLongOrNull()
        logger.info("blog tag: $tag id: $blogId")
        redirectAttributes.addFlashAttribute("blogid", blogId)
        return "redirect:/blogs/$tag"
    }

    @GetMapping("/admin/{tag}")
    fun allBlogEntries(@PathVariable tag: String?, @RequestParam(required = false, name = "lang") langCode: String? ,
                       request: HttpServletRequest, model: Model): String {
        val blogParams = setCommonModelParameters(model, request, langCode, tag)
        logger.info("allBlogEntries Fetch blog entries with: $blogParams")
        val blog = dbService.readBlogWithSameLanguage(blogParams.blogId, blogParams.langCode )
        model.addAttribute("blog", blog)
        logger.info("getting GUI with blogEntries")
        return "blogEntries"
    }

    @PostMapping("/admin")
    fun getBlogAdmin(redirectAttributes: RedirectAttributes, result: String): String {
        val tag = result.substringBefore(" ","")
        val blogId = result.substringAfter(" ","0").toLongOrNull()
        logger.info("blog tag: $tag id: $blogId")
        redirectAttributes.addFlashAttribute("blogid", blogId)
        return "redirect:/admin/$tag"
    }
}