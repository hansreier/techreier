package com.techreier.edrops.controllers

import com.techreier.edrops.config.InitException
import com.techreier.edrops.config.logger
import com.techreier.edrops.service.DbService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
@RequestMapping()
class EDropsStartController(dbService: DbService) : BaseController(dbService) {
    // inject via application.properties
    @Value("\${welcome.message} from Kotlin")
    private val message = "Hello World"

    //Get language set from session or parameter?
    @GetMapping
    fun welcome(request: HttpServletRequest, model: Model): String {
        try {
            logger.debug("welcome")
            model.addAttribute("message", message)
            model.addAttribute("message1", "from Kotlin")
            //  model.addAttribute("blogs", fetchBlogs())
            setCommonModelParameters(model, request)
        } catch (e: Exception) {
            throw (InitException("Cannot open default page", e)) //Rethrow so can be picked by error handler.
        }
        return "welcome"
    }

    // https://www.thymeleaf.org/doc/articles/standardurlsyntax.htmlzzzzz
    // https://stackoverflow.com/questions/26326559/thymeleaf-thhref-invoking-both-a-post-and-get
    // does not work
    // https://www.baeldung.com/thymeleaf-select-option
    @RequestMapping(value = ["/change"], method = [RequestMethod.GET])
    fun checkEventForm(model: Model): String? {
        logger.info("check event form")
        //model.getAttribute()
        return "redirect:/"
    }

    @PostMapping("/blogs2")
    fun getBlog(redirectAttributes: RedirectAttributes, blog: String): String {
        logger.info("blogs2 valgt blog: $blog")
        redirectAttributes.addFlashAttribute("blogid", blog)
        return "redirect:/"
    }
}