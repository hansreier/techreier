package com.techreier.edrops.util

import com.techreier.edrops.config.logger
import org.junit.Test
import org.junit.jupiter.api.Assertions.*
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.validation.BeanPropertyBindingResult
import java.util.*


private const val FORM = "testForm"
private const val FIELD = "field"

class ValidateTest {

    @Test
    fun validStringBySize() {
        val target = Form(FORM)
        val bindingResult = BeanPropertyBindingResult(target, "myForm")
        checkStringSize("hei", 30,  FIELD, bindingResult, MessageSourceMock)
        assertFalse(bindingResult.hasErrors())
    }

    @Test
    fun invalidStringBySize() {
        LocaleContextHolder.setLocale(Locale.forLanguageTag("no"))
        val target = Form(FORM)
        val bindingResult = BeanPropertyBindingResult(target, "testForm")
        checkStringSize("hei", 2,  FIELD, bindingResult, MessageSourceMock)
        assertTrue(bindingResult.hasErrors())
        assertEquals("error.maxSize.no", bindingResult.getFieldError(FIELD)?.defaultMessage)
        logger.info(bindingResult.getFieldError(FIELD)?.objectName)
    }

    data class Form(val name: String)

}