package com.techreier.edrops.util

import org.junit.Test
import org.junit.jupiter.api.Assertions.*
import io.mockk.mockk
import io.mockk.verify
import org.springframework.context.i18n.LocaleContextHolder

import org.springframework.validation.BindingResult
import java.util.Locale

class ValidateTest {

    @Test
    fun invalidStringTooLong() {
        val bindingResult = mockk<BindingResult>(relaxed = true)

        val ok = checkStringSize("toolong", 2, "title", bindingResult)

        verify {
            bindingResult.rejectValue("title", "error.maxSize", arrayOf(2), "toolong")
        }
        assertFalse(ok)
    }

    @Test
    fun validStringBySize() {
        val bindingResult = mockk<BindingResult>(relaxed = true)

        val ok = checkStringSize("toolong", 10, "title", bindingResult)
        assertTrue(ok)
    }

    @Test
    fun validDoubleWithExponentialNotation() {
        val bindingResult = mockk<BindingResult>(relaxed = true)

        val result = checkDouble("1E-6", "value", bindingResult)

        verify(exactly = 0) { bindingResult.rejectValue(any(), any(), any(), any()) }
        assertEquals(1e-6, result)
    }

    @Test
    fun validDoubleWithCommaSeparator() {
        val bindingResult = mockk<BindingResult>(relaxed = true)

        val result = checkDouble("12,5", "value", bindingResult)

        verify(exactly = 0) { bindingResult.rejectValue(any(), any(), any(), any()) }
        assertEquals(12.5, result)
    }

    @Test
    fun validDoubleWithDotSeparator() {
        val bindingResult = mockk<BindingResult>(relaxed = true)

        val result = checkDouble("12.5", "value", bindingResult)

        verify(exactly = 0) { bindingResult.rejectValue(any(), any(), any(), any()) }
        assertEquals(12.5, result)
    }

    @Test
    fun invalidDoubleFormat() {
        val bindingResult = mockk<BindingResult>(relaxed = true)

        val result = checkDouble("abc", "value", bindingResult)

        verify {
            bindingResult.rejectValue("value", "error.noDouble", arrayOf("abc"), "abc")
        }
        assertNull(result)
    }

    @Test
    fun invalidDoubleLessThanMin() {
        val bindingResult = mockk<BindingResult>(relaxed = true)
        LocaleContextHolder.setLocale(Locale.US)

        val result = checkDouble("1.5", "value", bindingResult, minValue = 2.0)

        verify {
            bindingResult.rejectValue("value", "error.lessThan", arrayOf("2"), "1.5")
        }
        assertEquals(1.5, result)
    }

    @Test
    fun invalidDoubleGreaterThanMax() {
        val bindingResult = mockk<BindingResult>(relaxed = true)
        LocaleContextHolder.setLocale(Locale.US)

        val result = checkDouble("10.5", "value", bindingResult, maxValue = 10.0)

        verify {
            bindingResult.rejectValue("value", "error.greaterThan", arrayOf("10"), "10.5")
        }
        assertEquals(10.5, result)
    }

    @Test
    fun emptyValueWhenRequired() {
        val bindingResult = mockk<BindingResult>(relaxed = true)

        val result = checkDouble("   ", "value", bindingResult, required = true)

        verify {
            bindingResult.rejectValue("value", "error.empty")
        }
        assertNull(result)
    }


}