package com.sigmondsmart.edrops.config

import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.annotation.AnnotationUtils
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.servlet.mvc.support.RedirectAttributes

// https://spring.io/blog/2013/11/01/exception-handling-in-spring-mvc
@ControllerAdvice
class GlobalDefaultExceptionHandler {

    @ExceptionHandler(value = [Exception::class])
    @Throws(Exception::class)
    fun defaultErrorHandler(request: HttpServletRequest, redirectAttributes: RedirectAttributes, e: Exception): String {
        // If the exception is annotated with @ResponseStatus rethrow it and let
        // the framework handle it (ErrorController is invoked)
        logger.info("Error: ${e.message}")
        if (AnnotationUtils.findAnnotation(
                e.javaClass,
                ResponseStatus::class.java
            ) != null
        ) throw e
        if (e is InitException) throw e //error in initializing default page, rethrow to error page
        logger.info("Special handled error: ${request.servletPath}",e)
        redirectAttributes.addFlashAttribute("error", e.message) //type error message in GUI
        return "redirect:" + request.servletPath
            .replaceAfterLast("/","").removeSuffix("/")
    }
}