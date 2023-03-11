package com.sigmondsmart.edrops.endpoint

import com.sigmondsmart.edrops.config.logger
import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import javax.servlet.http.HttpServletRequest

//Errors not picked up by error handler (404 Not Found)
//Redirect to default website page
//Alternative redirect to error page
//TODO not solved: Recursive errors opening default website page
@Controller
class EDropsErrorController: ErrorController {
    @RequestMapping("/error")
    fun handleError(request: HttpServletRequest): String {
        logger.info("Reier handles the error")
        return "redirect:/"
    }
}