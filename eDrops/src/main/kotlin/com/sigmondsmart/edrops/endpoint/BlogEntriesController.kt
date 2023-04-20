package com.sigmondsmart.edrops.endpoint

import com.sigmondsmart.edrops.config.logger
import com.sigmondsmart.edrops.service.DbService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
@RequestMapping("/blogs")
class BlogEntriesController(private val dbService: DbService): BaseController(dbService)
{
    @GetMapping("/admin")
    fun allBlogEntries(request: HttpServletRequest, model: Model): String {
        val blogParams = setCommonModelParameters(model, request)
        logger.info("allBlogEntries Fetch blog entries with: $blogParams")
        val blog = dbService.readBlogWithSameLanguage(blogParams.blogId, blogParams.langCode )
        model.addAttribute("blog", blog)
        logger.info("getting GUI with blogEntries")
        return "blogEntries"
    }

    @GetMapping
    fun allBlogTexts(request: HttpServletRequest, model: Model): String {
        val blogParams = setCommonModelParameters(model, request)
        logger.info("allBlogEntries Fetch blog entries with: $blogParams and summary")
        val blog = dbService.readBlogWithSameLanguage(blogParams.blogId, blogParams.langCode )
        model.addAttribute("blog", blog)
        return "blogSummaries"
    }

    // Transfer attributes between views
    // https://www.thymeleaf.org/doc/articles/springmvcaccessdata.html
    // https://www.baeldung.com/spring-web-flash-attributes
    @PostMapping("/bl")
    fun getBlog(redirectAttributes: RedirectAttributes, blog: Long): String {
        logger.info("getBlog valgt: $blog")
        redirectAttributes.addFlashAttribute("blogid", blog)
        return "redirect:/blogs"
    }

    @PostMapping("/bladmin")
    fun getBlogAdmin(redirectAttributes: RedirectAttributes, blog: Long): String {
        logger.info("BlogAdmin getBlog valgt: $blog")
        redirectAttributes.addFlashAttribute("blogid", blog)
        return "redirect:/blogs/admin"
    }
}