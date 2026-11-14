package com.techreier.edrops.controllers

import com.techreier.edrops.calc.CalcDouble
import com.techreier.edrops.calc.EPSILON
import com.techreier.edrops.calc.Expr
import com.techreier.edrops.calc.utils.radians
import com.techreier.edrops.calc.utils.errorIndicator
import com.techreier.edrops.calc.utils.operatorHelp
import com.techreier.edrops.config.*
import com.techreier.edrops.data.Docs
import com.techreier.edrops.data.Docs.DocIndex
import com.techreier.edrops.exceptions.CalcException
import com.techreier.edrops.exceptions.ParseException
import com.techreier.edrops.exceptions.VariableException
import com.techreier.edrops.forms.GraphForm
import com.techreier.edrops.graph.*
import com.techreier.edrops.util.Trace.info
import com.techreier.edrops.util.Trace.err
import com.techreier.edrops.util.Trace.resetTimer
import com.techreier.edrops.util.float
import com.techreier.edrops.util.msg
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import kotlin.math.abs

const val GRAPH = "graph"
const val GRAPH_DIR = "/$GRAPH"

@Controller
@RequestMapping(GRAPH_DIR)
class GraphController(
    val ctx: Context,
    private val graphService: GraphService,
    private val diagramService: DiagramService,
) : BaseController(ctx) {

    @GetMapping
    fun drawGraph(
        request: HttpServletRequest,
        response: HttpServletResponse,
        model: Model,
        redirectAttributes: RedirectAttributes,
    ): String {
        val xUnit = msg(ctx.messageSource, "xUnit")
        val yUnit = msg(ctx.messageSource, "yUnit")
        val graphForm = model.getAttribute("graphForm")
        graphForm ?: model.addAttribute("graphForm", GraphForm(xUnit = xUnit, yUnit = yUnit))
        val docIndex = prepare(model, request, response)
        if (docIndex.error || docIndex.index < 0) {
            redirectAttributes.addFlashAttribute("warning", "blogNotFound")
            return "redirect:/$HOME_DIR"
        }
        operatorHelp(model)
        return "graph"
    }

    @PostMapping
    fun calculateGraph(
        redirectAttributes: RedirectAttributes,
        graphForm: GraphForm,
        bindingResult: BindingResult,
        request: HttpServletRequest,
        response: HttpServletResponse,
        model: Model,
    ): String {
        logger.info("draw graph")

        val input = graphForm.validate(bindingResult)

        if (input != null) {
            resetTimer()
            info("Start graph")
            val graphLimits = GraphLimits(input.xMin, input.xMax, input.yMin, input.yMax)
            val graphMetadata = GraphMetadata(input.heightRatio, input.fontScale)
            val expr = Expr(CalcDouble(), input.yUnit)
            try {
                info("Graphlimits: ${graphLimits}")
                expr.calc.radians(input.radians)
                expr.variable(input.xUnit)
                expr.parse()
                val curve = graphService.generateSeries(
                    limits = graphLimits,
                    mathFunction = { x -> expr.calc(x) }
                )

                val seriesList = listOf(curve)
                val diagramArea = DiagramArea(
                    width = DIAGRAM_WIDTH,
                    height = PLOT_HEIGHT * input.heightRatio + PLOT_ANCHOR_Y * 2,
                    plotWidth = PLOT_WIDTH,
                    plotHeight = PLOT_HEIGHT * input.heightRatio,
                    plotAnchorX = PLOT_ANCHOR_X,
                    plotAnchorY = PLOT_ANCHOR_Y,
                )
                val diagramResult = diagramService.buildDiagram(graphLimits, diagramArea, graphMetadata)
                val polylines = diagramService.renderPolylines(seriesList, diagramResult.transformer)

                val xMin = seriesList.minOf { it.statistics.xMin }
                val xMax = seriesList.maxOf { it.statistics.xMax }
                var yMin = seriesList.minOf { it.statistics.yMin }
                var yMax = seriesList.maxOf { it.statistics.yMax }
                val ySize = (abs(yMax) + abs(yMin)) / 2
                if (ySize == 0.0 || (yMax - yMin) / ySize < EPSILON) {
                    val ykorr = if (ySize == 0.0) 1.0 else ySize / 10.0
                    yMin -= ykorr
                    yMax += ykorr
                }
                if (yMax < yMin ) {
                    yMin = -1.0
                    yMax = 1.0
                }
                graphForm.xMin = xMin.float()
                graphForm.xMax = xMax.float()
                graphForm.yMin = yMin.float(5)
                graphForm.yMax = yMax.float(5)

                redirectAttributes.addFlashAttribute("graphForm", graphForm)
                redirectAttributes.addFlashAttribute("diagram", diagramResult.diagram)
                redirectAttributes.addFlashAttribute("polylines", polylines)
                redirectAttributes.addFlashAttribute("invalid", curve.invalid)
            } catch (ex: Exception) {
                when (ex) {
                    is IllegalArgumentException -> bindingResult.reject("error.range")
                    is VariableException -> bindingResult.reject("error.noVariable")
                    is ParseException -> {
                        val errorText = errorIndicator(expr.expr, ex.parseError) ?: ""
                        val errorCode = "error.parse.${ex.parseError.key}"
                        ex.parseError.position
                        bindingResult.reject(errorCode,  arrayOf(errorText), "parse error")
                    }
                    is CalcException -> {
                        val message =  ex.detailMessage?.let { ": $it" } ?: ""
                        val args = arrayOf(ex.oper + message)
                        val errorCode = "error.calc.${ex.key}"
                        bindingResult.reject(errorCode, args, "calculation error")
                    }
                    is ArithmeticException -> {
                        val args = arrayOf(ex.message ?: "")
                        bindingResult.reject("error.calcGraph", args, "graph calculation error")
                    }
                    else -> {
                        err("Unrecoverable error in graph calculation: ${ex.message}")
                        throw ex
                    }
                }
            } finally {
                info("End graph")
            }
        }

        if (bindingResult.hasErrors() || input == null) {
            info("warn graph input error: $graphForm")
            val docIndex = prepare(model, request, response)
            if (docIndex.index < 0) {
                redirectAttributes.addFlashAttribute("warning", "blogNotFound")
                return "redirect:/$HOME_DIR"
            }
            model.addAttribute("graphForm", graphForm)
            operatorHelp(model)
            return GRAPH
        }

        return "redirect:$GRAPH_DIR"
    }

    private fun prepare(
        model: Model,
        request: HttpServletRequest,
        response: HttpServletResponse,
    ): DocIndex {
        val blogParams = fetchBlogParams(model, request, response, Menu.LAB)
        val docIndex = Docs.getDocIndex(Docs.graph, blogParams.oldLangCode, blogParams.usedLangCode, GRAPH)

        if (docIndex.index >= 0) {
            val doc = Docs.graph[docIndex.index]
            val docText: String = markdown.toHtml(doc, GRAPH_DIR).html
            model.addAttribute("doc", doc)
            model.addAttribute("docText", docText)
        }
        return docIndex
    }

}
