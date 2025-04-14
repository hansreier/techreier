package com.techreier.edrops.controllers

import com.techreier.edrops.config.logger

import com.techreier.edrops.service.CollatzService
import com.techreier.edrops.util.Docs
import com.techreier.edrops.util.Docs.DocIndex
import com.techreier.edrops.util.checkLong
import com.techreier.edrops.util.markdownToHtml
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.mvc.support.RedirectAttributes

const val COLLATZ = "collatz"
const val COLLATZ_DIR = "/$COLLATZ"

@Controller
@RequestMapping(COLLATZ_DIR)
class Collatz(ctx: Context, val collatzService: CollatzService) : Base(ctx) {
    @GetMapping
    fun collatz(
        request: HttpServletRequest,
        response: HttpServletResponse,
        model: Model,
        redirectAttributes: RedirectAttributes
    ): String {
        logger.info("Collatz page")
        val collatz = model.getAttribute("collatz") ?: Collatz()
        model.addAttribute("collatz", collatz)
        val docIndex = prepare(model, request, response)
        if (docIndex.error || docIndex.index < 0) {
            redirectAttributes.addFlashAttribute("warning", "blogNotFound")
            return "redirect:/$HOME_DIR"
        }
        return "collatz"
    }

    @PostMapping
    fun calculate(
        redirectAttributes: RedirectAttributes,
        collatz: Collatz,
        bindingResult: BindingResult,
        request: HttpServletRequest,
        response: HttpServletResponse,
        model: Model,
    ): String {
        logger.info("calculate collatz sequence")

        val seedNo = checkLong(collatz.seed,"seed", bindingResult, 1   )
        val result = seedNo?.let { collatzService.collatz(it) }

        if (bindingResult.hasErrors()) {
            logger.info("warn collatz seed input error: $collatz")
            val docIndex = prepare(model, request, response)
            if (docIndex.index < 0) {
                redirectAttributes.addFlashAttribute("warning", "blogNotFound")
                return "redirect:/$HOME_DIR"
            }
            model.addAttribute("collatz", collatz)
            return COLLATZ
        }

        redirectAttributes.addFlashAttribute("collatz", collatz)
        redirectAttributes.addFlashAttribute("result", result)
        return "redirect:$COLLATZ_DIR"
    }

    private fun prepare(
        model: Model,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): DocIndex {
        val blogParams = fetchBlogParams(model, request, response)
        val docIndex = Docs.getDocIndex(Docs.collatz, blogParams.oldLangCode, blogParams.usedLangCode, COLLATZ)

        if (docIndex.index >= 0 ) {
            val doc = Docs.collatz[docIndex.index]
            val docText: String = markdownToHtml(doc, COLLATZ_DIR).html
            model.addAttribute("doc", doc)
            model.addAttribute("docText", docText)
        }
        return docIndex
    }

    data class Collatz(
        var seed: String = "1",
    )
}
