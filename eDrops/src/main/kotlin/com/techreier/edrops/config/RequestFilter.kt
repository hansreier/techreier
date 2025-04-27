@file:Suppress("ktlint:standard:no-wildcard-imports")

package com.techreier.edrops.config

import com.techreier.edrops.util.mem
import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.FilterConfig
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.annotation.WebFilter
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletRequestWrapper
import jakarta.servlet.http.HttpServletResponse
import jakarta.servlet.http.HttpServletResponseWrapper

// Used for logging
@WebFilter
class RequestFilter : Filter {
    override fun init(filterConfig: FilterConfig?) {
        logger.info("Filter init")
    }

    override fun doFilter(
        request: ServletRequest,
        response: ServletResponse,
        chain: FilterChain,
    ) {
        val res = HttpServletResponseWrapper(response as HttpServletResponse)
        val req = HttpServletRequestWrapper(request as HttpServletRequest)
        logger.info("${req.method} ${req.servletPath} ${mem()} referer: ${req.getHeader("Referer")}")
        chain.doFilter(request, response)
        logger.debug("{} {} {} respons: {}", req.method, req.servletPath, mem(), res.status)
        /*Code removed, to be included in catch(e:Exception) block
         Sets error status to 400 Invalid request and types error directly in
         The problem is that it overrides the default error handling

        logger.warn("${req.method} ${req.servletPath}, Error: ${e.message}")
        res.contentType = "text/plain"
        res.characterEncoding = "UTF-8"
        res.status = 400
        res.setHeader("X-Error-Message", "Invalid request")
        val writer: PrintWriter = response.writer
        writer.write("Error: ${e.message}")
        writer.close() */
    }

    override fun destroy() {
        logger.info("Filter destroy")
    }
}
