package com.sigmondsmart.edrops.endpoint

import com.sigmondsmart.edrops.config.logger
import com.sigmondsmart.edrops.domain.BlogEntry
import com.sigmondsmart.edrops.service.DbService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import javax.servlet.http.HttpServletRequest

@Controller
@RequestMapping("/blogs")
class BlogEntriesController(private val dbService: DbService): BaseController(dbService)
{
    @GetMapping
    fun allBlogEntries(request: HttpServletRequest, model: Model): String {
        logger.info("got blogid: ${model.getAttribute("blogid")}")
        val blogId = (model.getAttribute("blogid")  ?: 1L) as Long
        val langCode = (model.getAttribute("langcode")) as String?
        logger.info("allBlogEntries Fetch blog entries with blogid: $blogId langcode: $langCode")

        var blog = dbService.readBlog(blogId)
        if (langCode != null && blog?.tag != null) {
            if (blog.language.code != langCode) {
                dbService.readBlog(langCode, blog.tag)?.let {blog = it}
            }
        }

        model.addAttribute("blogid", blogId)
        model.addAttribute("blog", blog)
        setCommonModelParameters(model, request)
        logger.info("getting GUI with blogEntries")
        return "blogEntries"
    }

    @GetMapping("/blogtexts")
    fun allBlogTexts(request: HttpServletRequest, model: Model): String {
        val blogId = (model.getAttribute("blogid")  ?: 1L) as Long
        logger.info("blogtexts Fetch blog entries with text and blogid: $blogId")
        model.addAttribute("blogEntries", fetchBlogEntries(blogId))
        setCommonModelParameters(model, request)
        logger.info("Fetch Reiers blog entries with text")
        return "blogTexts"
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

    private fun fetchBlogEntries(blogId: Long): MutableList<BlogEntry>? {
        logger.info("fetch blog entries")
        val entries =  dbService.readBlog(blogId)?.blogEntries
        logger.info("blog entries fetched")
        return entries
    }

    private fun fetchFirstBlogEntries(): MutableList<BlogEntry>? {
        return dbService.readFirstBlog(1)?.blogEntries
    }

    private fun fetchBlogEntriesAndText(): MutableList<BlogEntry>? {
        return dbService.readFirstBlog(1)?.blogEntries
    }
}