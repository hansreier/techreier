package com.techreier.edrops.controllers

import com.techreier.edrops.config.logger
import com.techreier.edrops.service.CollatzService
import com.techreier.edrops.service.DbService
import com.techreier.edrops.util.Docs
import com.techreier.edrops.util.markdownToHtml
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.context.MessageSource
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.validation.FieldError
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
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
        model.addAttribute("collatz", Collatz())
        val blogParams = setCommonModelParameters(COLLATZ, model, request, langCode)
        val docIndex = Docs.getDocIndex(Docs.collatz, blogParams.locale.language, COLLATZ)
        if (docIndex < 0) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, COLLATZ)
        }
        val doc = Docs.collatz[docIndex]

        val docText: String = markdownToHtml(doc, COLLATZ)
        model.addAttribute("doc", doc)
        model.addAttribute("docText", docText)

        return COLLATZ
    }

    @PostMapping
    fun calculate(
        redirectAttributes: RedirectAttributes, @Valid @ModelAttribute("collatz")
        collatz: Collatz, language: String?, bindingResult: BindingResult,
        request: HttpServletRequest, model: Model
    ): String {
        logger.info("Handling login")
        if (collatz.seed <= 0) {
            bindingResult.addError(FieldError("collatz", "seed", msg("tooSmallSeed")))
        }
        val result = collatzService.collatz(collatz.seed)
        redirectAttributes.addFlashAttribute("iterations", result.iterations)
        redirectAttributes.addFlashAttribute("result", result.result)
        return "redirect:$COLLATZ_DIR"
    }

    data class Collatz(var seed: Long = 1)

}