package com.techreier.edrops.forms

import com.techreier.edrops.data.lab.QuadraticEquation

data class CalcForm(
    var expr: String = QuadraticEquation.SEGMENT,
    var calcInput: String = ""
)


