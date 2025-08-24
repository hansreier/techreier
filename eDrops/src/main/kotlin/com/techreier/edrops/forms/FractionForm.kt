package com.techreier.edrops.forms

import com.techreier.edrops.service.FractionInput
import com.techreier.edrops.util.checkDouble
import com.techreier.edrops.util.checkLong
import org.springframework.validation.BindingResult

data class FractionForm(
    var decimalNumber: String, var maxDeviation: String, var maxDenominator: String
) {
    fun validate(bindingResult: BindingResult): FractionInput? {
        val decimalNumber = checkDouble(this.decimalNumber,"decimalNumber", bindingResult, null, null, true)
        val maxDeviation = checkDouble(this.maxDeviation,"maxDeviation", bindingResult, null, null, true)
        val maxDenominator = checkLong(this.maxDenominator,"maxDenominator", bindingResult, null, null, true)
        return if ((decimalNumber != null) && (maxDeviation != null) && (maxDenominator != null)) {
            FractionInput(decimalNumber, maxDeviation, maxDenominator)
        } else null
    }
}
