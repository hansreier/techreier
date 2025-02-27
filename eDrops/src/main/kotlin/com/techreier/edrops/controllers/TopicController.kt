package com.techreier.edrops.controllers

import com.techreier.edrops.config.logger
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.servlet.http.HttpSession
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
@RequestMapping
class TopicController {
    @PostMapping("/topic")
    fun getTopic(
        request: HttpServletRequest,
        response: HttpServletResponse,
        redirectAttributes: RedirectAttributes,
        topicKey: String,
        blogId: Long?,
        path: String,
        httpSession: HttpSession,
    ): String {
        logger.info("POST /topic, and redirect")
        logger.info("Topic selected: $topicKey path: $path blogId: $blogId")
        httpSession.setAttribute("topic", topicKey)
        redirectAttributes.addFlashAttribute("blogId", blogId)
        return "redirect:$path"
    }
}
