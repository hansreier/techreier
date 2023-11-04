package com.techreier.edrops.config

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.mvc.support.RedirectAttributes

// https://spring.io/blog/2013/11/01/exception-handling-in-spring-mvc
@ControllerAdvice
class ExceptionHandler {

    @ExceptionHandler(value = [Exception::class])
    @Throws(Exception::class)
    fun defaultErrorHandler(
        request: HttpServletRequest, redirectAttributes: RedirectAttributes,
        e: Exception, model: Model
    ): String {
        logger.info("Error class: ${e.javaClass} message: ${e.message} method: ${request.method}")
        if (e is IllegalArgumentException) { //error caught by Spring framework that cannot be rethrown
            model.addAttribute("path", request.servletPath)
            model.addAttribute("message", e.message)
            model.addAttribute("error", HttpStatus.BAD_REQUEST.reasonPhrase)
            model.addAttribute("status", HttpStatus.BAD_REQUEST.value())
        } else throw e
        return "error"
    }
    /* TODO what kind of errors should be recoverable and viewed on the same page, if any
       TODO the redirect to same page logic is not correct anyhow
    if (AnnotationUtils.findAnnotation( // Exceptions annotated with @Responsestatus, not used yet.
            e.javaClass,
            ResponseStatus::class.java
        ) != null
        || e is ResponseStatusException // Programmatic exceptions
        || request.method.equals("GET", true) // Risk for recursion
    ) throw e
    // recoverable errors to be viewed on the same page
    // An unhandled error in a GET endpoint is not recoverable because GUI will not be viewed correctly.
    logger.info("Recoverable error viewed on current page: ${request.servletPath}", e)
    redirectAttributes.addFlashAttribute("error", e.message) //type error message in GUI
    return "redirect:" + request.servletPath
        .replaceAfterLast("/", "").removeSuffix("/")
    */


}