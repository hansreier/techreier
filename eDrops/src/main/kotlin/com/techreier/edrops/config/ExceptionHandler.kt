package com.techreier.edrops.config

import com.techreier.edrops.controllers.HOME_DIR
import com.techreier.edrops.exceptions.BlogNotFoundException
import com.techreier.edrops.exceptions.NotAuthorizedException
import com.techreier.edrops.exceptions.PostNotFoundException
import jakarta.servlet.http.HttpServletRequest
import org.springframework.dao.DuplicateKeyException
import org.springframework.http.HttpStatus
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.mvc.support.RedirectAttributes

// https://spring.io/blog/2013/11/01/exception-handling-in-spring-mvc
// unsure when IllegalStateException occurs, need to be verified, it was a problem in a previous Spring version
// errors reaching here should be unrecoverable
// very difficult to recover and return to same HTML page
@ControllerAdvice
class ExceptionHandler {

    @ExceptionHandler(value = [Exception::class])
    @Throws(Exception::class)
    fun defaultErrorHandler(request: HttpServletRequest, e: Exception, model: Model, redirectAttributes: RedirectAttributes): String {

        logger.warn("Error class: ${e.javaClass} message: ${e.message} method: ${request.method}")

        when (e) {
            is IllegalStateException -> {
                model.addAttribute("path", request.servletPath)
                model.addAttribute("message", e.message)
                model.addAttribute("error", HttpStatus.BAD_REQUEST.reasonPhrase)
                model.addAttribute("status", HttpStatus.BAD_REQUEST.value())
            }
            is DuplicateKeyException -> {
                redirectAttributes.addFlashAttribute("warning", "blogDuplicate")
                return "redirect:/$HOME_DIR"
            }
            is NotAuthorizedException -> {
                redirectAttributes.addFlashAttribute("warning", "notAuthorized")
                return "redirect:/$HOME_DIR"
                }
            is BlogNotFoundException -> {
                redirectAttributes.addFlashAttribute("warning", "blogNotFound")
                return "redirect:/$HOME_DIR"
            }
            is PostNotFoundException -> {
                redirectAttributes.addFlashAttribute("warning", "postNotFound")
                return "redirect:/$HOME_DIR"
            }
        else -> throw e
        }
        return "error"
    }
}