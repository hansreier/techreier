package com.techreier.edrops.controllers

import com.techreier.edrops.config.Menu
import com.techreier.edrops.config.logger

import com.techreier.edrops.data.Docs
import com.techreier.edrops.data.Docs.DocIndex
import com.techreier.edrops.forms.FractionForm
import com.techreier.edrops.forms.GraphForm
import com.techreier.edrops.service.GraphService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.mvc.support.RedirectAttributes

const val GRAPH = "graph"
const val GRAPH_DIR = "/$GRAPH"

@Controller
@RequestMapping(GRAPH_DIR)
class GraphController(ctx: Context,
                         private val graphService: GraphService
) : BaseController(ctx) {

    @GetMapping
    fun fraction(
        request: HttpServletRequest,
        response: HttpServletResponse,
        model: Model,
        redirectAttributes: RedirectAttributes
    ): String {
        logger.info("Fraction page")
        val fractionForm = model.getAttribute("fractionForm") ?: FractionForm()
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
        graphForm: GraphForm,
        bindingResult: BindingResult,
        request: HttpServletRequest,
        response: HttpServletResponse,
        model: Model,
    ): String {
        logger.info("draw graph")

        val result = graphForm.validate(bindingResult) ?.let  { graphService.graph(it)}

        if (bindingResult.hasErrors()) {
            logger.info("warn graph input error: $graphForm")
            val docIndex = prepare(model, request, response)
            if (docIndex.index < 0) {
                redirectAttributes.addFlashAttribute("warning", "blogNotFound")
                return "redirect:/$HOME_DIR"
            }
            model.addAttribute("graphForm", graphForm)
            return FRACTION
        }

        redirectAttributes.addFlashAttribute("fractionForm", graphForm)
        redirectAttributes.addFlashAttribute("result", result)
        return "redirect:$FRACTION_DIR"
    }

    private fun prepare(
        model: Model,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): DocIndex {
        val blogParams = fetchBlogParams(model, request, response, Menu.LAB)
        val docIndex = Docs.getDocIndex(Docs.fraction, blogParams.oldLangCode, blogParams.usedLangCode, FRACTION)

        if (docIndex.index >= 0 ) {
            val doc = Docs.fraction[docIndex.index]
            val docText: String = markdown.toHtml(doc, FRACTION_DIR).html
            model.addAttribute("doc", doc)
            model.addAttribute("docText", docText)
        }
        return docIndex
    }

}
