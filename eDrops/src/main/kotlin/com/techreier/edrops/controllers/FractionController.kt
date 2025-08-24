package com.techreier.edrops.controllers

import com.techreier.edrops.config.logger

import com.techreier.edrops.data.Docs
import com.techreier.edrops.data.Docs.DocIndex
import com.techreier.edrops.forms.FractionForm
import com.techreier.edrops.service.FractionService
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

const val FRACTION = "fraction"
const val FRACTION_DIR = "/$FRACTION"

@Controller
@RequestMapping(FRACTION_DIR)
class Fraction(ctx: Context, val fractionService: FractionService) : BaseController(ctx) {

    @GetMapping
    fun fraction(
        request: HttpServletRequest,
        response: HttpServletResponse,
        model: Model,
        redirectAttributes: RedirectAttributes
    ): String {
        logger.info("Fraction page")
        val fractionForm = model.getAttribute("fractionForm") ?: FractionForm("","","") //TODO Reier correct or.
        model.addAttribute("fractionForm", fractionForm)
        val docIndex = prepare(model, request, response)
        if (docIndex.error || docIndex.index < 0) {
            redirectAttributes.addFlashAttribute("warning", "blogNotFound")
            return "redirect:/$HOME_DIR"
        }
        return "fraction"
    }

    @PostMapping
    fun calculate(
        redirectAttributes: RedirectAttributes,
        fractionForm: FractionForm,
        bindingResult: BindingResult,
        request: HttpServletRequest,
        response: HttpServletResponse,
        model: Model,
    ): String {
        logger.info("calculate fraction")

        val fractionResult = fractionForm.validate(bindingResult) ?.let  { fractionService.fraction(it)}

        if (bindingResult.hasErrors()) {
            logger.info("warn fraction input error: $fractionForm")
            val docIndex = prepare(model, request, response)
            if (docIndex.index < 0) {
                redirectAttributes.addFlashAttribute("warning", "blogNotFound")
                return "redirect:/$HOME_DIR"
            }
            model.addAttribute("fraction", fractionForm)
            return FRACTION
        }

        redirectAttributes.addFlashAttribute("fractionForm", fractionForm)
        redirectAttributes.addFlashAttribute("fractionResult", fractionResult)
        return "redirect:$FRACTION_DIR"
    }

    private fun prepare(
        model: Model,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): DocIndex {
        val blogParams = fetchBlogParams(model, request, response)
        val docIndex = Docs.getDocIndex(Docs.fraction, blogParams.oldLangCode, blogParams.usedLangCode, FRACTION)

        if (docIndex.index >= 0 ) {
            val doc = Docs.fraction[docIndex.index]
            val docText: String = markdownToHtml(doc, FRACTION_DIR).html
            model.addAttribute("doc", doc)
            model.addAttribute("docText", docText)
        }
        return docIndex
    }

}
