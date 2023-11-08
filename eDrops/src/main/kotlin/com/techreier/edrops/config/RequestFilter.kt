package com.techreier.edrops.config

import jakarta.servlet.*
import jakarta.servlet.FilterConfig
import jakarta.servlet.annotation.WebFilter
import jakarta.servlet.http.HttpServletResponse
import jakarta.servlet.http.HttpServletResponseWrapper
import org.springframework.context.annotation.Profile


// TODO not needed yet, but kept for future use
@WebFilter
@Profile("NotUsedProfile")
class RequestFilter : Filter {
    override fun init(filterConfig: FilterConfig?) {
        logger.info("Filter init")
    }

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
         val responseWrapped = HttpServletResponseWrapper(response as HttpServletResponse)
        try {
            logger.trace("Filter do status: ${responseWrapped.status}")
            chain.doFilter(request, response)
        } catch (e: Exception) {
            logger.warn("Filter caught error")
            //    responseWrapped.contentType = "text/plain"
            //    responseWrapped.characterEncoding = "UTF-8"
            //    responseWrapped.status = 400
            //    responseWrapped.setHeader("X-Error-Message", "Invalid request")
            //    val writer: PrintWriter = response.writer
            //    writer.write("Invalid request")
            //    writer.close()
        }
    }

    override fun destroy() {
        logger.info("Filter init")
    }
}




