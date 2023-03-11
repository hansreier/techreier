package com.sigmondsmart.edrops.config

import org.springframework.core.annotation.AnnotationUtils
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import javax.servlet.http.HttpServletRequest

// https://spring.io/blog/2013/11/01/exception-handling-in-spring-mvc
@ControllerAdvice
class GlobalDefaultExceptionHandler {

    @ExceptionHandler(value = [Exception::class])
    @Throws(Exception::class)
    fun defaultErrorHandler(request: HttpServletRequest, e: Exception): String {
        // If the exception is annotated with @ResponseStatus rethrow it and let
        // the framework handle it - like the OrderNotFoundException example
        // at the start of this post.
        // AnnotationUtils is a Spring Framework utility class.
        logger.info("Error: ${e.message}")
        if (AnnotationUtils.findAnnotation(
                e.javaClass,
                ResponseStatus::class.java
            ) != null
        ) throw e
        logger.info("Special handled error: ${request.servletPath}")
        // Otherwise setup and send the user to a default error-view.
    //    val mav = ModelAndView()
   //     mav.addObject("exception", e)
   //     mav.addObject("url", req.requestURL)
   //     mav.viewName = DEFAULT_ERROR_VIEW
        return "redirect:" + request.servletPath
            .replaceAfterLast("/","").removeSuffix("/")
    }
}