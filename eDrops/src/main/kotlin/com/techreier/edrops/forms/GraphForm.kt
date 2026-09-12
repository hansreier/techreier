package com.techreier.edrops.forms

import com.techreier.edrops.config.FONT_SCALE
import com.techreier.edrops.config.MAX_FONT_SCALE
import com.techreier.edrops.config.MAX_HEIGHT_RATIO
import com.techreier.edrops.config.MIN_FONT_SCALE
import com.techreier.edrops.config.MIN_HEIGHT_RATIO
import com.techreier.edrops.graph.GraphInput
import com.techreier.edrops.util.checkDouble
import com.techreier.edrops.util.checkStringSize
import com.techreier.edrops.util.fixed
import org.springframework.validation.BindingResult

data class GraphForm(
    var xMin: String = 0.0.fixed(1), var xMax: String = 30.0.fixed(1),
    var yMin: String = (-1.5).fixed(5), var yMax: String = 1.5.fixed(),
    var xUnit: String = "x", var yUnit: String = "y",
    var heightRatio: String = 0.65.fixed(2), var fontScale: String = FONT_SCALE.fixed(),
) {
    fun validate(bindingResult: BindingResult): GraphInput? {
        val heightRatio =
            checkDouble(this.heightRatio, "heightRatio", bindingResult, MIN_HEIGHT_RATIO, MAX_HEIGHT_RATIO)
        val fontScale = checkDouble(this.fontScale, "fontScale", bindingResult, MIN_FONT_SCALE, MAX_FONT_SCALE)
        val xMin = checkDouble(this.xMin, "xMin", bindingResult)
        val xMax = checkDouble(this.xMax, "xMax", bindingResult)
        val yMin = checkDouble(this.yMin, "yMin", bindingResult)
        val yMax = checkDouble(this.yMax, "yMax", bindingResult)
        val xUnit = checkStringSize(this.xUnit, 20, "xUnit", bindingResult, 1)
        val yUnit = checkStringSize(this.yUnit, 20, "yUnit", bindingResult, 1)
        if (xMin == null || xMax == null || yMin == null || yMax == null || fontScale == null
            || !xUnit || !yUnit || heightRatio == null) return null
        return GraphInput(xMin, xMax, yMin, yMax, heightRatio, fontScale,this.xUnit, this.yUnit)
    }
}
