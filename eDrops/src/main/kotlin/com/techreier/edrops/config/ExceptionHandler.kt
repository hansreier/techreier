package com.techreier.edrops.config

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

// https://spring.io/blog/2013/11/01/exception-handling-in-spring-mvc
// unsure when IllegalStateException occurs, need to be verified, it was a problem in a previous Spring version
// errors reaching here should be unrecoverable
// very difficult to recover and return to same HTML page
@ControllerAdvice
class ExceptionHandler {

    @ExceptionHandler(value = [Exception::class])
    @Throws(Exception::class)
    fun defaultErrorHandler(request: HttpServletRequest, e: Exception, model: Model): String {

        logger.warn("Error class: ${e.javaClass} message: ${e.message} method: ${request.method}")

        when (e) {
            is IllegalStateException -> {
                model.addAttribute("path", request.servletPath)
                model.addAttribute("message", e.message)
                model.addAttribute("error", HttpStatus.BAD_REQUEST.reasonPhrase)
                model.addAttribute("status", HttpStatus.BAD_REQUEST.value())
            }
        else -> throw e
        }
        return "error"
    }
}