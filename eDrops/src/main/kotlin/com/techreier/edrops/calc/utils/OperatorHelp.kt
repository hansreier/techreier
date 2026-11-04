package com.techreier.edrops.calc.utils

import com.techreier.edrops.calc.CalcDouble
import com.techreier.edrops.calc.Op
import org.springframework.ui.Model

// For Thymeleaf
fun operatorHelp(model : Model) {
    val supportedFunctions = Op.functions.filter { it in CalcDouble().operators }.toSet()
    model.addAttribute("operatorText", Op.toString(Op.basicOperators + Op.parenthesis))
    model.addAttribute("funcText", Op.toString(supportedFunctions))
    model.addAttribute("constText", Op.toString(Op.constants))
    model.addAttribute("varText", Op.toString(Op.variables))
}