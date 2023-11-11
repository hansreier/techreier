package com.techreier.edrops.controllers

import com.techreier.edrops.config.logger
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController
import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.servlet.error.ErrorAttributes
import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.context.request.WebRequest
import java.util.*


// Default error page controller
//TODO Improve error page
@Controller
class ErrorController @Autowired private constructor(
    var errorAttributes: ErrorAttributes,
) : ErrorController, AbstractErrorController(errorAttributes) {

    val NO_MESSAGE = "No message"
    @Autowired
    lateinit var messageSource: MessageSource
    @RequestMapping("/error")
    fun handleError(req: HttpServletRequest,request: WebRequest, response: HttpServletResponse, model: Model): String {
        logger.info("inside handleerror 4")
        val locale = LocaleContextHolder.getLocale() //attempt to make language dependent error messages
        logger.info("Response method: ${req.method} uri: ${req.requestURI} status ${response.status}")
        var options = ErrorAttributeOptions.defaults()
        if (isServerError(response)) {
            logger.info("Is server error")
            options = options
                .including(ErrorAttributeOptions.Include.STACK_TRACE)
                .including(ErrorAttributeOptions.Include.EXCEPTION)
        }
        options = options.including(ErrorAttributeOptions.Include.MESSAGE)
        val errAttributes = errorAttributes.getErrorAttributes(request, options)
        model.addAllAttributes(errAttributes)
        if (notFound(response)) {
            logger.info("Is not found")
          //  model["message"] = improveNotFoundMsg(errAttributes["message"] , locale )
        }
        logger.info("Returning to error page")
        return "error"
    }

    private fun isServerError(response: HttpServletResponse): Boolean {
        return (response.status in 500..599)
    }

    private fun notFound(response: HttpServletResponse): Boolean {
        return (response.status==404)
    }

    // Get a better 404 Not Found message based on existing message and language, if possible
    // If the existing message tag is a tag not recognized by current locale, just return "not found"
    private fun improveNotFoundMsg(message: Any?, locale: Locale): Any {
        var correctedMessage: String? = message as String
        if ((correctedMessage.isNullOrBlank()) || correctedMessage.contains(NO_MESSAGE, true)) { //Spring boot hack
            correctedMessage = messageSource.getMessage("path", null, NO_MESSAGE, locale)
        }
        val notFound =  messageSource.getMessage("notfound", null,"not found", locale)
        return "$correctedMessage $notFound"
    }
}