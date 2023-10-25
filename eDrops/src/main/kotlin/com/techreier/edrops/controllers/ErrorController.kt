package com.techreier.edrops.controllers

import com.techreier.edrops.config.logger
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController
import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.servlet.error.ErrorAttributes
import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.context.request.WebRequest

//Errors not picked up by error handler (e.g. 404 Not Found)
//Redirect to default website page can create recursion.
//It is better to redirect to an error page
//If disabled Springs default error handler is used to open error page
//This handler initializes som variables picked up by error page.

//TODO Redesign error page
@Controller
class ErrorController @Autowired private constructor(
    var errorAttributes: ErrorAttributes,
) : ErrorController, AbstractErrorController(errorAttributes) {
    @RequestMapping("/error")
    fun handleError(request: WebRequest, response: HttpServletResponse, model: Model): String {
        logger.info("Response status ${response.status}")
        var options = ErrorAttributeOptions.defaults()
        if (isServerError(response))
            options = options
                .including(ErrorAttributeOptions.Include.STACK_TRACE)
                .including(ErrorAttributeOptions.Include.EXCEPTION)
        if (!notFound(response))
            options = options.including(ErrorAttributeOptions.Include.MESSAGE)

        model.addAllAttributes(errorAttributes.getErrorAttributes(request, options))
        return "error"
    }

    private fun isServerError(response: HttpServletResponse): Boolean {
        return (response.status>=500 && response.status <600)
    }

    private fun notFound(response: HttpServletResponse): Boolean {
        return (response.status==404)
    }
}