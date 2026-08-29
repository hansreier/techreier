package com.techreier.edrops.forms

import com.techreier.edrops.graph.GraphInput
import com.techreier.edrops.util.checkDouble
import org.springframework.validation.BindingResult

data class GraphForm(
    var xMin: String = 0.toString(), var xMax: String = 30.toString(),
    var yMin: String = (-1.5).toString(), var yMax: String = 1.5.toString(),
) {
    fun validate(bindingResult: BindingResult): GraphInput? {
        val xMin = checkDouble(this.xMin, "xMin", bindingResult)
        val xMax = checkDouble(this.xMax, "xMax", bindingResult)
        val yMin = checkDouble(this.yMin, "yMin", bindingResult)
        val yMax = checkDouble(this.yMax, "yMax", bindingResult)
        if (xMin == null || xMax == null || yMin == null || yMax == null) return null
        return GraphInput(xMin, xMax, yMin, yMax)
    }
}
