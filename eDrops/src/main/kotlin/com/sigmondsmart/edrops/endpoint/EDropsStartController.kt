package com.sigmondsmart.edrops.endpoint

import com.sigmondsmart.edrops.domain.Blog
import com.sigmondsmart.edrops.service.DbService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/")
class EDropsStartController(private val dbService: DbService)
{
    // inject via application.properties
    @Value("\${welcome.message} from Kotlin")
    private val message = "Hello World"

    @RequestMapping("/kotlin2", "/")
    fun welcome(model: Model): String {
        model.addAttribute("message", message)
        model.addAttribute("message1", "from Kotlin")
        model.addAttribute("blogs", fetchBlogs())
        return "welcome"
    }

    private fun fetchBlogs(): MutableSet<Blog>? {
        val blogs = dbService.readOwner(1)?.blogs
        return blogs
      //  return dbService.readOwner(1)?.blogs
    }
}