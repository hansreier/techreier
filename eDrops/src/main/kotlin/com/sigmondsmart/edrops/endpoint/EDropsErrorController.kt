package com.sigmondsmart.edrops.endpoint

import com.sigmondsmart.edrops.config.logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController
import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.servlet.error.ErrorAttributes
import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


//Errors not picked up by error handler (e.g. 404 Not Found)
//Redirect to default website page can create recursion.
//It is better to redirect to an error page
//If disabled Springs default error handler is used to open error page
//This handler initializes som variables picked up by error page.
//TODO Redesign error page
//@Profile("notFOundProfile")
@Controller
class EDropsErrorController @Autowired private constructor(
    var errorAttributes: ErrorAttributes,
) : ErrorController, AbstractErrorController(errorAttributes) {
    @RequestMapping("/error")
    fun handleError(request: HttpServletRequest, response: HttpServletResponse, model: Model): String {
        if (response.status == HttpServletResponse.SC_NOT_FOUND) return "redirect:/"
        val opts = getErrorAttributes(
            request, ErrorAttributeOptions.defaults()
                .including(ErrorAttributeOptions.Include.STACK_TRACE)
            // .including(ErrorAttributeOptions.Include.EXCEPTION)
        ) //Problems override Spring config
        logger.info("Reier handles the error: $errorAttributes.")
        model.addAllAttributes(opts)

        return "error"
    }
}