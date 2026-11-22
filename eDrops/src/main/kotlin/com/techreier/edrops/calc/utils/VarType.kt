package com.techreier.edrops.calc.utils

import com.techreier.edrops.calc.OpI
import com.techreier.edrops.calc.PRIOR_BOOLEAN
import com.techreier.edrops.calc.PRIOR_INTEGER

enum class VarType { NUMBER, BOOLEAN, INTEGER }

val OpI.varType: VarType
    get() = when (prior) {
        PRIOR_BOOLEAN -> VarType.BOOLEAN
        PRIOR_INTEGER -> VarType.INTEGER
        else -> VarType.NUMBER
    }