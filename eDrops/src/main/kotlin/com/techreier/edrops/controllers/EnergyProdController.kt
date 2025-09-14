package com.techreier.edrops.controllers

import com.techreier.edrops.config.logger

import com.techreier.edrops.data.Docs
import com.techreier.edrops.data.Docs.DocIndex
import com.techreier.edrops.dto.toDTO
import com.techreier.edrops.forms.EnergyProdForm
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

const val ENERGY_PROD = "energyprod"

const val ENERGY_PROD_DIR = "/$ENERGY_PROD"

@Controller
@RequestMapping(ENERGY_PROD_DIR)
class EnergyProdController(val ctx: Context, val energyService: EnergyService) : BaseController(ctx) {

    @GetMapping
    fun energyProduction(
        request: HttpServletRequest,
        response: HttpServletResponse,
        model: Model,
        redirectAttributes: RedirectAttributes,
    ): String {
        logger.info("Energy production page")
        val energyProdForm = model.getAttribute("energyProdForm") ?: EnergyProdForm()
        model.addAttribute("energyProdForm", energyProdForm)
        val result = model.getAttribute("result")
        logger.info("Resultat: $result")
        val docIndex = prepare(model, request, response)
        if (docIndex.error || docIndex.index < 0) {
            redirectAttributes.addFlashAttribute("warning", "blogNotFound")
            return "redirect:/$HOME_DIR"
        }
        return "energyProd"
    }

    @PostMapping
    fun fetch(
        redirectAttributes: RedirectAttributes,
        energyProdForm: EnergyProdForm,
        bindingResult: BindingResult,
        request: HttpServletRequest,
        response: HttpServletResponse,
        model: Model,
    ): String {
        logger.info("fetch energy data")

        val year = checkInt(energyProdForm.year, "year", bindingResult, 1)

        val energyProd = year?.let { energyService.energyProduction[it] }

        if (bindingResult.hasErrors() || energyProd == null) {
            if ((energyProd == null) && (!bindingResult.hasErrors())) {
                bindingResult.rejectValue("year", "error.noData")
            }
            logger.info("warn energy production input error: $energyProdForm")
            val docIndex = prepare(model, request, response)
            if (docIndex.index < 0) {
                redirectAttributes.addFlashAttribute("warning", "blogNotFound")
                return "redirect:/$HOME_DIR"
            }
            model.addAttribute("energyProdForm", energyProdForm)
            return ENERGY_PROD
        }

        redirectAttributes.addFlashAttribute("energyProdForm", energyProdForm)
        redirectAttributes.addFlashAttribute("energyProd", energyProd.toDTO(ctx.messageSource))
        return "redirect:$ENERGY_PROD_DIR"
    }

    private fun prepare(
        model: Model,
        request: HttpServletRequest,
        response: HttpServletResponse,
    ): DocIndex {
        val blogParams = fetchBlogParams(model, request, response)
        val docIndex = Docs.getDocIndex(Docs.energyProd, blogParams.oldLangCode, blogParams.usedLangCode, ENERGY_PROD)

        if (docIndex.index >= 0) {
            val doc = Docs.energyProd[docIndex.index]
            val docText: String = markdownToHtml(doc, ENERGY_PROD_DIR).html
            model.addAttribute("doc", doc)
            model.addAttribute("docText", docText)
        }
        return docIndex
    }

}
