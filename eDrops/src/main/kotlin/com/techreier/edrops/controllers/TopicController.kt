package com.techreier.edrops.controllers

import com.techreier.edrops.config.logger
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
@RequestMapping
class TopicController {
    @PostMapping("/topic")
    fun getTopic(
        redirectAttributes: RedirectAttributes,
        topicKey: String?,
        blogId: Long?,
        path: String,
    ): String {
        logger.info("POST /topic, and redirect")
        logger.debug("Topic selected: $topicKey path: $path blogId: $blogId")
        redirectAttributes.addFlashAttribute("topicKey", topicKey)
        // Or else id blogId used to populate menu will not be preserved
        // Alternative to use hidden field is either cookie or store in session
        // If more state is needed, using Spring session (and store session in db) is recommended.
        redirectAttributes.addFlashAttribute("blogId", blogId)
        logger.info("before redirect to get: $topicKey")
        return "redirect:$path"
    }
}
