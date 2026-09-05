package com.techreier.edrops.forms

import com.techreier.edrops.graph.GraphInput
import com.techreier.edrops.util.checkDouble
import org.springframework.validation.BindingResult
import com.techreier.edrops.util.fixed

data class GraphForm(
    var xMin: String = 0.0.fixed(1), var xMax: String = 30.0.fixed(1),
    var yMin: String = (-1.5).fixed(5), var yMax: String = 1.5.fixed(),
    var xUnit: String="x", var yUnit: String="y",
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
