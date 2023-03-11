package com.sigmondsmart.edrops.endpoint

import com.sigmondsmart.edrops.config.logger
import com.sigmondsmart.edrops.domain.Blog
import com.sigmondsmart.edrops.domain.BlogEntry
import com.sigmondsmart.edrops.service.DbService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import javax.servlet.http.HttpServletRequest

@Controller
@RequestMapping("/blogs")
class BlogEntriesController(private val dbService: DbService): BaseController()
{

    @GetMapping
    fun allBlogEntries(request: HttpServletRequest, model: Model): String {
        val blogId = (model.getAttribute("blogid")  ?: 1L) as Long
        logger.info("Fetch blog entries with blogid: $blogId")
        model.addAttribute("blogs", fetchBlogs())
        model.addAttribute("blogEntries", fetchBlogEntries(blogId))
        setCommonModelParameters(model, request.servletPath)
        logger.info("getting GUI with blogEntries")
        return "blogEntries"
    }

    @GetMapping("/blogtexts")
    fun allBlogTexts(model: Model): String {
        val blogId = (model.getAttribute("blogid")  ?: 1L) as Long
        logger.info("Fetch blog entries with text and blogid: $blogId")
        model.addAttribute("blogs", fetchBlogs())
        model.addAttribute("blogEntries", fetchBlogEntries(blogId))
        logger.info("Fetch Reiers blog entries with text")
        return "blogTexts"
    }

    // Overføre attributter mellom ulike views.
    // https://www.thymeleaf.org/doc/articles/springmvcaccessdata.html
    // https://www.baeldung.com/spring-web-flash-attributes
    @PostMapping("/bl")
    fun getBlog(redirectAttributes: RedirectAttributes, blog: Long): String {
            logger.info("valgt: $blog")
            redirectAttributes.addFlashAttribute("blogid", blog)
            return "redirect:/blogs"
    }

    private fun fetchBlogs(): MutableSet<Blog>? {
        logger.info("Fetch blogs by Owner")
        val blogs = dbService.readOwner(1)?.blogs
        logger.info("Blogs fetched")
        return blogs
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