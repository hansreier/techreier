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

const val ENERGYDATA = "energydata"
const val ENERGY_REMARK = "energyremark"
const val ENERGYDATA_DIR = "/$ENERGYDATA"
const val ENERGYDATA_TEMPLATE = "energyData"

@Controller
@RequestMapping(ENERGYDATA_DIR)
class EnergyController(val ctx: Context, val energyService: EnergyService) : BaseController(ctx) {

    @GetMapping
    fun energyData(
        request: HttpServletRequest,
        response: HttpServletResponse,
        model: Model,
        redirectAttributes: RedirectAttributes,
    ): String {
        logger.info("EnergyData page")
        val energyForm = model.getAttribute("energyForm") as EnergyForm? ?: EnergyForm()
        val energyValues = energyService.energyYears[energyForm.year.toIntOrNull()] ?: mutableListOf()
        model.addAttribute("energyData", energyValues.toDTOs(ctx.messageSource))
        model.addAttribute("energyForm", energyForm)
        val result = model.getAttribute("result")
        logger.info("Resultat: $result")
        val docIndex = prepare(model, request, response)
        if (docIndex.error || docIndex.index < 0) {
            redirectAttributes.addFlashAttribute("warning", "blogNotFound")
            return "redirect:/$HOME_DIR"
        }
        return ENERGYDATA_TEMPLATE
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

        checkInt(energyForm.year, "year", bindingResult, 2008, 2024)

        if (bindingResult.hasErrors()) {
            logger.info("warn energy production input error: $energyForm")
            val docIndex = prepare(model, request, response)
            if (docIndex.index < 0) {
                redirectAttributes.addFlashAttribute("warning", "blogNotFound")
                return "redirect:/$HOME_DIR"
            }
            model.addAttribute("energyForm", energyForm)
            return ENERGYDATA_TEMPLATE
        }
        redirectAttributes.addFlashAttribute("energyForm", energyForm)
        return "redirect:$ENERGYDATA_DIR"
    }

    private fun prepare(
        model: Model,
        request: HttpServletRequest,
        response: HttpServletResponse,
    ): DocIndex {
        val blogParams = fetchBlogParams(model, request, response)
        val docPreIndex = Docs.getDocIndex(Docs.energy, blogParams.oldLangCode, blogParams.usedLangCode, ENERGYDATA)

        if (docPreIndex.index >= 0) {
            val doc = Docs.energy[docPreIndex.index]
            val docText: String = markdownToHtml(doc, ENERGYDATA_DIR).html
            model.addAttribute("doc", doc)
            model.addAttribute("docText", docText)
        }

        val docPostIndex = Docs.getDocIndex(Docs.energy, blogParams.oldLangCode, blogParams.usedLangCode, ENERGY_REMARK)

        if (docPostIndex.index >= 0) {
            val doc2 = Docs.energy[docPostIndex.index]
            val docText2: String = markdownToHtml(doc2, ENERGYDATA_DIR).html
            model.addAttribute("docText2", docText2)
        }

        return docPreIndex
    }

}
