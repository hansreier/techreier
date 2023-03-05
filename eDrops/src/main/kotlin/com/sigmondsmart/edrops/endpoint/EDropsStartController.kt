package com.sigmondsmart.edrops.endpoint

import com.sigmondsmart.edrops.config.logger
import com.sigmondsmart.edrops.domain.Blog
import com.sigmondsmart.edrops.service.DbService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.servlet.mvc.support.RedirectAttributes

private const val PATH="/start"
@Controller
@RequestMapping(PATH)
class EDropsStartController(private val dbService: DbService): BaseController(PATH)
{
    // inject via application.properties
    @Value("\${welcome.message} from Kotlin")
    private val message = "Hello World"

    //Get language set from session or parameter?
    @GetMapping( "")
    fun welcome(model: Model): String {
        val id = model.getAttribute("blogid")
        model.addAttribute("message", message)
        model.addAttribute("message1", "from Kotlin")
        model.addAttribute("blogs", fetchBlogs())
        model.addAttribute("languages",fetchLanguages())
        model.addAttribute("path",PATH)
        return "welcome"
    }
  //  https://www.thymeleaf.org/doc/articles/standardurlsyntax.htmlzzzzz
  //   https://stackoverflow.com/questions/26326559/thymeleaf-thhref-invoking-both-a-post-and-get
    // does not work

    // https://www.baeldung.com/thymeleaf-select-option
    @RequestMapping(value = ["/change"], method = [RequestMethod.GET])
    fun checkEventForm(model: Model): String? {
        logger.info("Reier was here stupid")
        //model.getAttribute()
        return "redirect:/"
    }
    // Overføre attributter mellom ulike views.
    // https://www.thymeleaf.org/doc/articles/springmvcaccessdata.html
    // https://www.baeldung.com/spring-web-flash-attributes
    @PostMapping("/blogs2")
    fun getBlog(redirectAttributes: RedirectAttributes, blog: String): String {
        logger.info("valgt blog: $blog")
        redirectAttributes.addFlashAttribute("blogid", blog)
        return "redirect:/"
    }

    private fun fetchBlogs(): MutableSet<Blog>? {
        val blogs = dbService.readOwner(1)?.blogs
        return blogs
    }
}