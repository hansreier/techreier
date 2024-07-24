package com.techreier.edrops.config

import com.techreier.edrops.util.mem
import jakarta.servlet.*
import jakarta.servlet.FilterConfig
import jakarta.servlet.annotation.WebFilter
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletRequestWrapper
import jakarta.servlet.http.HttpServletResponse
import jakarta.servlet.http.HttpServletResponseWrapper
import java.io.PrintWriter


// TODO not needed yet, but kept for future use
@WebFilter
class RequestFilter : Filter {
    override fun init(filterConfig: FilterConfig?) {
        logger.info("Filter init")
    }

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val res = HttpServletResponseWrapper(response as HttpServletResponse)
        val req = HttpServletRequestWrapper(request as HttpServletRequest)
        try {
            logger.info("${req.method} ${req.servletPath} ${mem()}")
            chain.doFilter(request, response)
            logger.debug("${req.method} ${req.servletPath} status: ${res.status}")
        } catch (e: Exception) {
            logger.warn("${req.method} ${req.servletPath}, Error: ${e.message}")
            res.contentType = "text/plain"
            res.characterEncoding = "UTF-8"
            res.status = 400
            res.setHeader("X-Error-Message", "Invalid request")
            val writer: PrintWriter = response.writer
            writer.write("Error: ${e.message}")
            writer.close()
        }
    }

    override fun destroy() {
        logger.info("Filter destroy")
    }
}




