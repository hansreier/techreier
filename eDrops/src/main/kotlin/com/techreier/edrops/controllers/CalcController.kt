package com.techreier.edrops.controllers

import com.techreier.edrops.calc.CalcDouble
import com.techreier.edrops.calc.MultiExpr
import com.techreier.edrops.calc.Op
import com.techreier.edrops.calc.utils.VarType
import com.techreier.edrops.calc.utils.errorIndicator
import com.techreier.edrops.calc.utils.operatorHelp
import com.techreier.edrops.calc.utils.sanitize
import com.techreier.edrops.calc.utils.varType
import com.techreier.edrops.config.Menu
import com.techreier.edrops.config.logger
import com.techreier.edrops.data.Docs
import com.techreier.edrops.data.Docs.DocIndex
import com.techreier.edrops.exceptions.CalcException
import com.techreier.edrops.exceptions.ParseException
import com.techreier.edrops.forms.CalcForm
import com.techreier.edrops.util.Trace.info
import com.techreier.edrops.util.autoformat
import com.techreier.edrops.util.axisformat
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import kotlin.math.roundToInt

const val CALC = "calc"
const val CALC_DIR = "/$CALC"

@Controller
@RequestMapping(CALC_DIR)
class CalcController(
    val ctx: Context,
) : BaseController(ctx) {

    @GetMapping
    fun calc(
        request: HttpServletRequest,
        response: HttpServletResponse,
        model: Model,
        redirectAttributes: RedirectAttributes,
    ): String {
        val calcForm = model.getAttribute("calcForm")

        calcForm ?: model.addAttribute("calcForm", CalcForm())
        val docIndex = prepare(model, request, response)
        if (docIndex.error || docIndex.index < 0) {
            redirectAttributes.addFlashAttribute("warning", "blogNotFound")
            return "redirect:/$HOME_DIR"
        }
        operatorHelp(model)
        return "calc"
    }

    @PostMapping
    fun calculate(
        redirectAttributes: RedirectAttributes,
        calcForm: CalcForm,
        bindingResult: BindingResult,
        request: HttpServletRequest,
        response: HttpServletResponse,
        model: Model,
    ): String {
        logger.info("calculate multiexpression")
        val calc = CalcDouble()
        val input = calcForm.calcInput
        try {
            logger.info("Calculating multiexpression: $input")
            val expr = MultiExpr(calc, input)
            expr.parse()
            expr.calculate()
        } catch (e: ParseException) {
            val err = e.parseError
            logger.warn("feilnøkkel: ${"error.parse." +err.key} operator=${err}")
            val args = arrayOf(err.oper)
            bindingResult.reject("error.parse." + err.key, args, "parse error")
            val errorIndicator = errorIndicator(input.sanitize(), e.parseError)
            model.addAttribute("errorText", errorIndicator)
        } catch (e: CalcException) {
            val args = arrayOf(e.oper)
            bindingResult.reject("error.calc." + e.key, args, "calculation error")
            model.addAttribute("errorText", e.detailMessage)
        }
        if (bindingResult.hasErrors() ) {
            info("calc input error: $calcForm")
            val docIndex = prepare(model, request, response)
            if (docIndex.index < 0) {
                redirectAttributes.addFlashAttribute("warning", "blogNotFound")
                return "redirect:/$HOME_DIR"
            }
            model.addAttribute("calcForm", calcForm)
            operatorHelp(model)
            return CALC
        }
        redirectAttributes.addFlashAttribute("calcForm", calcForm)

        //TODO ReierAsk change when VarType.INTEGER is used e.g. for graph types. Radians is tempoararily removed
        val formattedVars = calc.vars.entries
            .filter { (op, _) -> op != Op.RADIANS }
            .associate { (op, value) ->
            val formatted = when (op.varType) {
                VarType.BOOLEAN -> if (value > 0.5) "true" else "false"
                VarType.INTEGER -> if (value.isFinite()) value.roundToInt().toString() else value.axisformat(12)
                VarType.NUMBER -> value.autoformat()
            }
            op to formatted
        }
        redirectAttributes.addFlashAttribute("vars", formattedVars)
        operatorHelp(model)
        return "redirect:$CALC_DIR"
    }


    private fun prepare(
        model: Model,
        request: HttpServletRequest,
        response: HttpServletResponse,
    ): DocIndex {
        val blogParams = fetchBlogParams(model, request, response, Menu.LAB)
        val docIndex = Docs.getDocIndex(Docs.calc, blogParams.oldLangCode, blogParams.usedLangCode, CALC)

        if (docIndex.index >= 0) {
            val doc = Docs.calc[docIndex.index]
            val docText: String = markdown.toHtml(doc, CALC_DIR).html
            model.addAttribute("doc", doc)
            model.addAttribute("docText", docText)
        }
        return docIndex
    }

}
