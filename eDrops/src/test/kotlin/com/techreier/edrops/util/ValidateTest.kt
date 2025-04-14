package com.techreier.edrops.util

import org.junit.Test
import org.junit.jupiter.api.Assertions.*
import io.mockk.mockk
import io.mockk.verify

import org.springframework.validation.BindingResult

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


}