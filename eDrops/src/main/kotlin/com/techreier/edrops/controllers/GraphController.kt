package com.techreier.edrops.controllers

import com.techreier.edrops.config.Menu
import com.techreier.edrops.config.logger

import com.techreier.edrops.data.Docs
import com.techreier.edrops.data.Docs.DocIndex
import com.techreier.edrops.forms.GraphForm
import com.techreier.edrops.graph.DiagramService
import com.techreier.edrops.graph.GraphService
import com.techreier.edrops.graph.PlotArea
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
class GraphController(
    ctx: Context,
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
        val graphForm = model.getAttribute("graphForm") ?: GraphForm()
        model.addAttribute("graphForm", graphForm)
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

        val validatedInput = graphForm.validate(bindingResult)

        if (bindingResult.hasErrors() || validatedInput == null) {
            logger.info("warn graph input error: $graphForm")
            val docIndex = prepare(model, request, response)
            if (docIndex.index < 0) {
                redirectAttributes.addFlashAttribute("warning", "blogNotFound")
                return "redirect:/$HOME_DIR"
            }
            model.addAttribute("graphForm", graphForm)
            return GRAPH
        }

        // 1. Generer ren matematisk dataserie
        val sinusCurve = graphService.generateSeries(
            input = validatedInput,
            mathFunction = { x -> kotlin.math.sin(x) }
        )
        val seriesList = listOf(sinusCurve)

        // 2. Opprett layout & transformator for visning
        val plotArea = PlotArea(x = 70.0, y = 40.0, width = 700.0, height = 400.0)

        // 3. Render diagram-elementer og polyline-strenger
        val diagramResult = diagramService.buildDiagram(validatedInput, plotArea)
        val polylines = diagramService.renderPolylines(seriesList, diagramResult.transformer)

        // 4. Statistikk-oppdatering på form
        val xMin = seriesList.minOf { it.statistics.xMin }
        val xMax = seriesList.maxOf { it.statistics.xMax }
        val yMin = seriesList.minOf { it.statistics.yMin }
        val yMax = seriesList.maxOf { it.statistics.yMax }

        if (xMin != Double.POSITIVE_INFINITY) graphForm.xMin = xMin.toString()
        if (xMax != Double.NEGATIVE_INFINITY) graphForm.xMax = xMax.toString()
        if (yMin != Double.POSITIVE_INFINITY) graphForm.yMin = yMin.toString()
        if (yMax != Double.NEGATIVE_INFINITY) graphForm.yMax = yMax.toString()

        redirectAttributes.addFlashAttribute("graphForm", graphForm)
        redirectAttributes.addFlashAttribute("diagram", diagramResult.diagram)
        redirectAttributes.addFlashAttribute("polylines", polylines)
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
