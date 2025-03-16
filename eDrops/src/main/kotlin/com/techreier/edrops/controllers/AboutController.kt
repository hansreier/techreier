package com.techreier.edrops.controllers

import com.techreier.edrops.config.logger
import com.techreier.edrops.util.Docs.about
import com.techreier.edrops.util.Docs.getDocIndex
import com.techreier.edrops.util.markdownToHtml
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.mvc.support.RedirectAttributes

const val ABOUT = "about"
const val ABOUT_DIR = "/$ABOUT"

@Controller
@RequestMapping(ABOUT_DIR)
class About(context: Context) : Base(context) {

    @GetMapping("/{segment}")
    fun content(
        @PathVariable segment: String?,
        request: HttpServletRequest,
        response: HttpServletResponse,
        model: Model,
        redirectAttributes: RedirectAttributes,
    ): String {
        val blogParams = fetchBlogParams(model, request, response)
        val docIndex = getDocIndex(about, blogParams.oldLangCode, blogParams.usedLangCode, segment)
        if (docIndex.error) {
            if (docIndex.index < 0) {
                redirectAttributes.addFlashAttribute("warning", "blogNotFound")
                return "redirect:/$HOME_DIR"
            } else {
                model.addAttribute("docLangCode", blogParams.oldLangCode)
            }
        }

        val doc = about[docIndex.index]
        val inlineHtml =  markdownToHtml(doc, ABOUT_DIR)
        if (inlineHtml.warning) model.addAttribute("warning", "blogOtherLanguage")
        model.addAttribute("doc", doc)
        model.addAttribute("docText", inlineHtml.markdown)
        return ABOUT
    }

    // Redirect to page with segment in path
    @GetMapping
    fun redirect(
        request: HttpServletRequest,
        response: HttpServletResponse,
        model: Model,
        redirectAttributes: RedirectAttributes,
    ): String {
        val blogParams = fetchBlogParams(model, request, response)
        val docIndex = getDocIndex(about, blogParams.oldLangCode, blogParams.usedLangCode)
        if (docIndex.error || docIndex.index < 0) {
            if (docIndex.index < 0) {
                redirectAttributes.addFlashAttribute("warning", "blogNotFound")
                return "redirect:/$HOME_DIR"
            } else {
                redirectAttributes.addFlashAttribute("warning", "readFirstBlog")
                return "redirect:$BLOG_DIR/$docIndex.index"
            }
        }

        val doc = about[docIndex.index]
        return "redirect:$ABOUT_DIR/${doc.segment}"
    }

    @PostMapping
    fun getEntry(segment: String): String {
        logger.info("About controller redirect")
        return "redirect:$ABOUT_DIR/$segment"
    }
}
