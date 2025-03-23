package com.techreier.edrops.controllers

import com.techreier.edrops.config.AppConfig
import com.techreier.edrops.config.logger
import com.techreier.edrops.util.buildVersion
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
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.util.*

private const val NO_MESSAGE = "No message available"

@Controller
class ErrorController
    @Autowired
    private constructor(
        var errorAttributes: ErrorAttributes,
    ) : AbstractErrorController(errorAttributes),
        ErrorController {
        @Autowired
        lateinit var messageSource: MessageSource
        @Autowired
        lateinit var appConfig: AppConfig

        @RequestMapping("/error")
        fun handleError(
            req: HttpServletRequest,
            request: WebRequest,
            response: HttpServletResponse,
            model: Model,
        ): String {
            val locale = LocaleContextHolder.getLocale() // attempt to make language dependent error messages
            var options = ErrorAttributeOptions.defaults()
            if (isServerError(response)) {
                options =
                    options
                        .including(ErrorAttributeOptions.Include.STACK_TRACE)
                        .including(ErrorAttributeOptions.Include.EXCEPTION)
            }
            options = options.including(ErrorAttributeOptions.Include.MESSAGE)
            val errAttributes = errorAttributes.getErrorAttributes(request, options)
            val path = errAttributes["path"]
            val msg = errAttributes["message"]

            logger.warn("Error Method: ${req.method} uri: ${req.requestURI} status: ${response.status} msg: $msg path: $path")
            model.addAllAttributes(errAttributes)
            model["message"] = improveNoMsgAvail(msg, locale, response.status)
            model["path"] = decodeURLEncodedPath(path)
            model["appversion"] = "${appConfig.appname} ${buildVersion(appConfig.buildTime, false)}"
            return "error"
        }

        private fun isServerError(response: HttpServletResponse): Boolean = (response.status in 500..599)

        // If Spring returns No Message Available, replace with suitable localized message dependend of status code
        private fun improveNoMsgAvail(
            message: Any?,
            locale: Locale,
            httpStatusCode: Int,
        ): Any {
            var correctedMessage: String? = message as String
            if ((correctedMessage.isNullOrBlank()) || correctedMessage.contains(NO_MESSAGE, true)) {
                correctedMessage = messageSource.getMessage(httpStatusCode.toString(), null, "", locale)
            }
            return "$correctedMessage"
        }

        private fun decodeURLEncodedPath(path: Any?): Any =
            (path as String?)?.let {
                URLDecoder.decode(it, StandardCharsets.UTF_8.toString())
            } ?: ""
    }
