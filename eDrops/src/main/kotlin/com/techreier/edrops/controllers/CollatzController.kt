package com.techreier.edrops.controllers

import com.techreier.edrops.config.logger
import com.techreier.edrops.service.CollatzResult
import com.techreier.edrops.service.CollatzService
import com.techreier.edrops.service.DbService
import com.techreier.edrops.util.Docs
import com.techreier.edrops.util.markdownToHtml
import jakarta.servlet.http.HttpServletRequest
import org.springframework.context.MessageSource
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes

const val COLLATZ = "collatz"
const val COLLATZ_DIR = "/$COLLATZ"

@Controller
@RequestMapping(COLLATZ_DIR)
class CollatzController(dbService: DbService, messageSource: MessageSource, val collatzService: CollatzService) :
    BaseController(dbService, messageSource) {
    @GetMapping
    fun collatz(
        @RequestParam(required = false, name = "lang") langCode: String?,
        request: HttpServletRequest, model: Model
    ): String {
        logger.info("Collatz page")
        val collatz = model.getAttribute("collatz") ?: Collatz()
        model.addAttribute("collatz", collatz)
        prepare(model, request, langCode)
        return COLLATZ
    }

    @PostMapping
    fun calculate(
        redirectAttributes: RedirectAttributes,
        collatz: Collatz,  bindingResult: BindingResult,
        request: HttpServletRequest, model: Model
    ): String {
        logger.info("calculate collatz sequence")
        val seedNo = collatz.seed.toLongOrNull()
        var result: CollatzResult? = null
        seedNo?.let {
            if (it <= 0) {
                bindingResult.addFieldError("collatz","seed", "zeroOrNegativeError", collatz.seed)
            }
            else
                result = collatzService.collatz(it)
        } ?: bindingResult.addFieldError("collatz","seed", "noLongError", collatz.seed)

        if (bindingResult.hasErrors()) {
            logger.info("warn collatz seed input error: $collatz")
            prepare(model, request)
            model.addAttribute("collatz", collatz)
            return COLLATZ
        }

        redirectAttributes.addFlashAttribute("collatz", collatz)
        redirectAttributes.addFlashAttribute("result", result)
        return "redirect:$COLLATZ_DIR"

    }

    private fun prepare(model: Model, request: HttpServletRequest, langCode: String? = null) {
        val blogParams = setCommonModelParameters(COLLATZ, model, request, langCode)
        val docIndex = Docs.getDocIndex(Docs.collatz, blogParams.locale.language, COLLATZ)
        val doc = Docs.collatz[docIndex]
        val docText: String = markdownToHtml(doc, COLLATZ)
        model.addAttribute("doc", doc)
        model.addAttribute("docText", docText)
    }
    data class Collatz(var seed: String = "1")
}