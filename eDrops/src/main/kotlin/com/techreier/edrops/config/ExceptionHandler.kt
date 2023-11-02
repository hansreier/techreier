package com.techreier.edrops.config

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.annotation.AnnotationUtils
import org.springframework.http.HttpStatus
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.mvc.support.RedirectAttributes

// https://spring.io/blog/2013/11/01/exception-handling-in-spring-mvc
@ControllerAdvice
class ExceptionHandler {

    @ExceptionHandler(value = [Exception::class])
    @Throws(Exception::class)
        fun defaultErrorHandler(request: HttpServletRequest, response: HttpServletResponse, redirectAttributes: RedirectAttributes,
                                e: Exception, model: Model): String {

        logger.info("Error class: ${e.javaClass} message: ${e.message} method: ${request.method} status: ${response.status}")
        //Error caught by Spring framework
        //IllegalArgumentException: Error attributes must be manually populated
        // ConstraintViolationException: Error attributes are populated automatically
        if (response.status == HttpStatus.OK.value()) {
            model.addAttribute("error", e.message)
            return "error"
        }

        if (AnnotationUtils.findAnnotation( //Catches exceptions annotated with @Responsestatus, not used yet.
                e.javaClass,
                ResponseStatus::class.java
            ) != null
            || e is ResponseStatusException || request.method.equals("GET", true)
        ) throw e //rethrow to error page TODO perhaps not best solution, spring outputs Warning message
        // recoverable errors to be viewed on the same page
        // An unhandled error in a GET endpoint is not recoverable because GUI will not be viewed correctly.
        // TODO what kind of errors should be viewed this way
        logger.info("Recoverable error viewed on current page: ${request.servletPath}", e)
        redirectAttributes.addFlashAttribute("error", e.message) //type error message in GUI
        return "redirect:" + request.servletPath
            .replaceAfterLast("/", "").removeSuffix("/")
    }
}