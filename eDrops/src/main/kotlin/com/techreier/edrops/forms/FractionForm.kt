package com.techreier.edrops.forms

import com.techreier.edrops.service.FractionInput
import com.techreier.edrops.util.checkDouble
import com.techreier.edrops.util.checkLong
import org.springframework.validation.BindingResult

data class FractionForm(
    var decimalNumber: String, var epsilon: String, var maxDenominator: String
) {
    fun validate(bindingResult: BindingResult): FractionInput? {
        val decimalNumber = checkDouble(this.decimalNumber,"decimalNumber", bindingResult, null, null, true)
        val epsilon = checkDouble(this.epsilon,"epsilon", bindingResult, null, null, true)
        val maxDenominator = checkLong(this.maxDenominator,"maxDenominator", bindingResult, null, null, true)
        return if ((decimalNumber != null) && (epsilon != null) && (maxDenominator != null)) {
            FractionInput(decimalNumber, epsilon, maxDenominator)
        } else null
    }
}
