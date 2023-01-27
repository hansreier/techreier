package com.sigmondsmart.edrops.endpoint

import com.sigmondsmart.edrops.config.logger
import com.sigmondsmart.edrops.domain.BlogEntry
import com.sigmondsmart.edrops.service.DbService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/")
class BlogEntriesController(private val dbService: DbService)
{

    @GetMapping("/blogs")
    fun allBlogEntries(model: Model): String {
        model.addAttribute("blogEntries", fetchBlogEntries())
        logger.info("Fetch Reiers blog entries")
        return "blogEntries"
    }

    @GetMapping("/blogtexts")
    fun allBlogTexts(model: Model): String {
        model.addAttribute("blogEntries", fetchBlogEntries())
        logger.info("Fetch Reiers blog entries with text")
        return "blogTexts"
    }

    private fun fetchBlogEntries(): MutableList<BlogEntry>? {
        return dbService.readFirstBlog(1)?.blogEntries
    }
}