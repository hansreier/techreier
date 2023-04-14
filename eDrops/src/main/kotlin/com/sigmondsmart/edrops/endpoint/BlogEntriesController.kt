package com.sigmondsmart.edrops.endpoint

import com.sigmondsmart.edrops.config.logger
import com.sigmondsmart.edrops.service.DbService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/blogs")
class BlogEntriesController(private val dbService: DbService): BaseController(dbService)
{
    @GetMapping
    fun allBlogEntries(request: HttpServletRequest, model: Model): String {
        val blogParams = setCommonModelParameters(model, request)
        logger.info("allBlogEntries Fetch blog entries with: $blogParams")
        val blog = dbService.readBlogWithSameLanguage(blogParams.blogId, blogParams.langCode )
        model.addAttribute("blog", blog)
        logger.info("getting GUI with blogEntries")
        return "blogEntries"
    }

    @GetMapping("/summary")
    fun allBlogTexts(request: HttpServletRequest, model: Model): String {
        val blogParams = setCommonModelParameters(model, request)
        logger.info("allBlogEntries Fetch blog entries with: $blogParams and summary")
        val blog = dbService.readBlogWithSameLanguage(blogParams.blogId, blogParams.langCode )
        model.addAttribute("blog", blog)
        return "blogSummaries"
    }
}