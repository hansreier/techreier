package com.techreier.edrops.config

import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.annotation.AnnotationUtils
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
        fun defaultErrorHandler(request: HttpServletRequest, redirectAttributes: RedirectAttributes, e: Exception): String {

        // If the exception is annotated with @ResponseStatus rethrow it and let
        // the framework handle it (ErrorController is invoked)
        logger.info("Error class: ${e.javaClass} message: ${e.message} method: ${request.method}")
        if (AnnotationUtils.findAnnotation(
                e.javaClass,
                ResponseStatus::class.java
            ) != null
            || e is ResponseStatusException || request.method.equals("GET", true)
        ) throw e //rethrow to error page TODO perhaps not best solution, spring outputs Warning message
        // recoverable errors to be viewed on the same page
        // TODO what kind of errors should be viewed this way
        // An unhandled error in a GET endpoint is not recoverable because GUI will not be viewed correctly.
        logger.info("Recoverable error viewed on current page: ${request.servletPath}", e)
        redirectAttributes.addFlashAttribute("error", e.message) //type error message in GUI
        return "redirect:" + request.servletPath
            .replaceAfterLast("/", "").removeSuffix("/")
    }

    /*

    @ExceptionHandler(NoHandlerFoundException::class)
    @Throws(Exception::class)
    fun handleNotFoundException(e: NoHandlerFoundException?): ResponseEntity<Any>? {
        logger.info("Skittfeil")
        // Customize the response and error message here
        val errorMessage = "The requested resource was not found."
        val status = HttpStatus.NOT_FOUND
        return ResponseEntity(errorMessage, status)
    }*/
}