package com.sigmondsmart.edrops.controllers

import com.sigmondsmart.edrops.service.DbService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/")
class KotlinTestController(private val dbService: DbService)
{
    private val log = LoggerFactory.getLogger(KotlinTestController::class.java)

    // inject via application.properties
    @Value("\${welcome.message} in Kotlin")
    private val message = "Hello World in Kotlin"

    @RequestMapping("/kotlin","/kotelett")
    fun welcome(model: Model): String {
        model.addAttribute("message", message)
        log.info("Hello word with message: $message")
        log.debug("Koko")
        return "welcome"
    }

    @RequestMapping("/create")
    fun create(model: Model): String {
        model.addAttribute("message", message)
        log.info("Hello word with message: $message")
        log.debug("Koko")
        dbService.createBlog()
        return "welcome"
    }

    @RequestMapping("/read")
    fun read(model: Model): String {
        dbService.readFirstBlog(1)
        return "welcome"
    }
}