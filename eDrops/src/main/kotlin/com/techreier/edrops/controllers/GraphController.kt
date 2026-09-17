package com.techreier.edrops.controllers

import com.techreier.edrops.config.DIAGRAM_WIDTH
import com.techreier.edrops.config.Menu
import com.techreier.edrops.config.PLOT_ANCHOR_X
import com.techreier.edrops.config.PLOT_ANCHOR_Y
import com.techreier.edrops.config.PLOT_HEIGHT
import com.techreier.edrops.config.PLOT_WIDTH
import com.techreier.edrops.config.logger

import com.techreier.edrops.data.Docs
import com.techreier.edrops.data.Docs.DocIndex
import com.techreier.edrops.forms.GraphForm
import com.techreier.edrops.graph.DiagramArea
import com.techreier.edrops.graph.DiagramService
import com.techreier.edrops.graph.GraphLimits
import com.techreier.edrops.graph.GraphMetadata
import com.techreier.edrops.graph.GraphService
import com.techreier.edrops.util.fixed
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
import kotlin.math.sin

const val GRAPH = "graph"
const val GRAPH_DIR = "/$GRAPH"

@Controller
@RequestMapping(GRAPH_DIR)
class GraphController(
    val ctx: Context,
    private val graphService: GraphService,
    private val diagramService: DiagramService
) : BaseController(ctx) {

    @GetMapping
    fun drawGraph(
        request: HttpServletRequest,
        response: HttpServletResponse,
        model: Model,
        redirectAttributes: RedirectAttributes
    ): String {
        logger.info("Graph page")
        val xUnit = msg(ctx.messageSource, "xUnit")
        val yUnit = msg(ctx.messageSource, "yUnit")
        val graphForm = model.getAttribute("graphForm")
        graphForm ?: model.addAttribute("graphForm", GraphForm(xUnit = xUnit, yUnit = yUnit))
        val docIndex = prepare(model, request, response)
        if (docIndex.error || docIndex.index < 0) {
            redirectAttributes.addFlashAttribute("warning", "blogNotFound")
            return "redirect:/$HOME_DIR"
        }
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
            val graphLimits = GraphLimits(input.xMin, input.xMax, input.yMin, input.yMax)
            val graphMetadata = GraphMetadata(input.heightRatio, input.fontScale)
            try {
                logger.info("Graphlimits: ${graphLimits}")
                val sinusCurve = graphService.generateSeries(
                    limits = graphLimits,
                    mathFunction = { x -> sin(x) }
                )

                val seriesList = listOf(sinusCurve)
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
                val yMin = seriesList.minOf { it.statistics.yMin }
                val yMax = seriesList.maxOf { it.statistics.yMax }

                graphForm.xMin = xMin.fixed()
                graphForm.xMax = xMax.fixed()
                graphForm.yMin = yMin.fixed(5)
                graphForm.yMax = yMax.fixed(5)

                redirectAttributes.addFlashAttribute("graphForm", graphForm)
                redirectAttributes.addFlashAttribute("diagram", diagramResult.diagram)
                redirectAttributes.addFlashAttribute("polylines", polylines)
            } catch (ex: Exception) {
                if (ex is IllegalArgumentException) {
                    bindingResult.reject("error.range")
                }
                else {
                    logger.warn("error in graph calculation: ${ex.message}")
                    bindingResult.reject("error.calcGraph")
                }
            }
        }

        if (bindingResult.hasErrors() || input == null) {
            logger.info("warn graph input error: $graphForm")
            val docIndex = prepare(model, request, response)
            if (docIndex.index < 0) {
                redirectAttributes.addFlashAttribute("warning", "blogNotFound")
                return "redirect:/$HOME_DIR"
            }
            model.addAttribute("graphForm", graphForm)
            return GRAPH
        }

        return "redirect:$GRAPH_DIR"
    }

    private fun prepare(
        model: Model,
        request: HttpServletRequest,
        response: HttpServletResponse
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
