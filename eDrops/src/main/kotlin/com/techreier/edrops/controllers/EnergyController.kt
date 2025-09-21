package com.techreier.edrops.controllers

import com.techreier.edrops.config.logger

import com.techreier.edrops.data.Docs
import com.techreier.edrops.data.Docs.DocIndex
import com.techreier.edrops.dto.toDTOs
import com.techreier.edrops.forms.EnergyForm
import com.techreier.edrops.service.EnergyService
import com.techreier.edrops.util.checkInt
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

const val ENERGY = "energy"
const val ENERGY_DIR = "/$ENERGY"
const val ENERGY_TEMPLATE ="energyProd"

@Controller
@RequestMapping(ENERGY_DIR)
class EnergyController(val ctx: Context, val energyService: EnergyService) : BaseController(ctx) {

    @GetMapping
    fun energyProduction(
        request: HttpServletRequest,
        response: HttpServletResponse,
        model: Model,
        redirectAttributes: RedirectAttributes,
    ): String {
        logger.info("Energy page")
        val energyForm = model.getAttribute("energyForm") ?: EnergyForm()
        model.addAttribute("energyForm", energyForm)
        val result = model.getAttribute("result")
        logger.info("Resultat: $result")
        val docIndex = prepare(model, request, response)
        if (docIndex.error || docIndex.index < 0) {
            redirectAttributes.addFlashAttribute("warning", "blogNotFound")
            return "redirect:/$HOME_DIR"
        }
        return ENERGY_TEMPLATE
    }

    @PostMapping
    fun fetch(
        redirectAttributes: RedirectAttributes,
        energyForm: EnergyForm,
        bindingResult: BindingResult,
        request: HttpServletRequest,
        response: HttpServletResponse,
        model: Model,
    ): String {
        logger.info("fetch energy data")

        val year = checkInt(energyForm.year, "year", bindingResult, 2008, 2024)

        val energyValues = year?.let { energyService.energyYears[it] ?: mutableListOf() } ?: mutableListOf()

        if (bindingResult.hasErrors() || energyValues.isEmpty()) {
            if ((energyValues.isEmpty()) && (!bindingResult.hasErrors())) {
                bindingResult.rejectValue("year", "error.noData")
            }
            logger.info("warn energy production input error: $energyForm")
            val docIndex = prepare(model, request, response)
            if (docIndex.index < 0) {
                redirectAttributes.addFlashAttribute("warning", "blogNotFound")
                return "redirect:/$HOME_DIR"
            }
            model.addAttribute("energyProdForm", energyForm)
            return ENERGY_TEMPLATE
        }
        redirectAttributes.addFlashAttribute("energyProdForm", energyForm)
        redirectAttributes.addFlashAttribute("energyProd", energyValues.toDTOs(ctx.messageSource))
        return "redirect:$ENERGY_DIR"
    }

    private fun prepare(
        model: Model,
        request: HttpServletRequest,
        response: HttpServletResponse,
    ): DocIndex {
        val blogParams = fetchBlogParams(model, request, response)
        val docIndex = Docs.getDocIndex(Docs.energy, blogParams.oldLangCode, blogParams.usedLangCode, ENERGY)

        if (docIndex.index >= 0) {
            val doc = Docs.energy[docIndex.index]
            val docText: String = markdownToHtml(doc, ENERGY_DIR).html
            model.addAttribute("doc", doc)
            model.addAttribute("docText", docText)
        }
        return docIndex
    }

}
