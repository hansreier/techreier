package com.techreier.edrops.forms

import com.techreier.edrops.service.GraphInput
import com.techreier.edrops.util.checkDouble
import org.springframework.validation.BindingResult

data class GraphForm(
    var xMin: String = 0.toString(), var xMax: String = 100.toString(),
    var yMin: String = 0.toString(), var yMax: String = 100.toString(),
) {
    fun validate(bindingResult: BindingResult): GraphInput? {
        val xMin = checkDouble(this.xMin,"xMin", bindingResult, null, null, true)
        val xMax = checkDouble(this.xMax,"xMax", bindingResult, null, null, true)
        val yMin = checkDouble(this.yMin,"xMin", bindingResult, null, null, true)
        val yMax = checkDouble(this.yMax,"xMax", bindingResult, null, null, true)
        return if ((xMin != null) && (xMax != null) && (yMin != null) && (yMax != null) ) {
            GraphInput(xMin, xMax, yMin, yMax)
        } else null
    }
}
