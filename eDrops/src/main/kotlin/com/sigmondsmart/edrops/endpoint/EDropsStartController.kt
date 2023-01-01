package com.sigmondsmart.edrops.endpoint

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/")
class EDropsStartController {
    // inject via application.properties
    @Value("\${welcome.message} from Kotlin")
    private val message = "Hello World"

    @RequestMapping("/kotlin2", "/")
    fun welcome(model: Model): String {
        model.addAttribute("message", message)
        model.addAttribute("message1", "from Kotlin")
        return "welcome"
    }
}