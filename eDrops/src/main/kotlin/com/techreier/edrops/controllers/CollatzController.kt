package com.techreier.edrops.controllers

import com.techreier.edrops.config.logger

import com.techreier.edrops.service.CollatzService
import com.techreier.edrops.data.Docs
import com.techreier.edrops.data.Docs.DocIndex
import com.techreier.edrops.forms.CollatzForm
import com.techreier.edrops.util.checkLong
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
class CollatzController(ctx: Context,
                        private val collatzService: CollatzService) : BaseController(ctx) {

    @GetMapping
    fun collatz(
        request: HttpServletRequest,
        response: HttpServletResponse,
        model: Model,
        redirectAttributes: RedirectAttributes
    ): String {
        logger.info("Collatz page")
        val collatzForm = model.getAttribute("collatzForm") ?: CollatzForm()
        model.addAttribute("collatzForm", collatzForm)
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
        collatzForm: CollatzForm,
        bindingResult: BindingResult,
        request: HttpServletRequest,
        response: HttpServletResponse,
        model: Model,
    ): String {
        logger.info("calculate collatzForm sequence")

        val seedNo = checkLong(collatzForm.seed,"seed", bindingResult, 1   )
        val result = seedNo?.let { collatzService.collatz(it) }

        if (bindingResult.hasErrors()) {
            logger.info("warn collatz seed input error: $collatzForm")
            val docIndex = prepare(model, request, response)
            if (docIndex.index < 0) {
                redirectAttributes.addFlashAttribute("warning", "blogNotFound")
                return "redirect:/$HOME_DIR"
            }
            model.addAttribute("collatzForm", collatzForm)
            return COLLATZ
        }

        redirectAttributes.addFlashAttribute("collatzForm", collatzForm)
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
            val docText: String = markdown.toHtml(doc, COLLATZ_DIR).html
            model.addAttribute("doc", doc)
            model.addAttribute("docText", docText)
        }
        return docIndex
    }
}
