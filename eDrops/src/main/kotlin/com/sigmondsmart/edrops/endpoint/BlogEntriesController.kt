package com.sigmondsmart.edrops.endpoint

import com.sigmondsmart.edrops.domain.BlogEntry
import com.sigmondsmart.edrops.service.DbService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/")
class BlogEntriesController(private val dbService: DbService)
{
    private val log = LoggerFactory.getLogger(BlogEntriesController::class.java)

    @GetMapping("/blogs")
    fun allBlogEntries(model: Model): String {
        model.addAttribute("blogEntries", fetchBlogEntries())
        log.info("Fetch Reiers blog entries")
        return "blogEntries"
    }

    @GetMapping("/blogtexts")
    fun allBlogTexts(model: Model): String {
        model.addAttribute("blogEntries", fetchBlogEntries())
        log.info("Fetch Reiers blog entries with text")
        return "blogTexts"
    }

    private fun fetchBlogEntries(): MutableList<BlogEntry>? {
        return dbService.readFirstBlog(1)?.blogEntries
    }
}