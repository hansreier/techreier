package com.techreier.edrops.util

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.context.i18n.LocaleContextHolder
import java.util.Locale

class AxisTest {

    @BeforeEach
    fun setUp() {
        LocaleContextHolder.setLocale(Locale.US)
    }

    @AfterEach
    fun tearDown() {
        LocaleContextHolder.resetLocaleContext()
    }

    @Test
    fun stripsTrailingZeros() {
        assertEquals("2.1", 2.100.axis())
        assertEquals("2", 2.0.axis())
        assertEquals("12.34", 12.3400.axis())
    }

    @Test
    fun roundsFloatingPointNoise() {
        assertEquals("2", 2.00000000001.axis())
        assertEquals("2.1", 2.10000000001.axis())
    }

    @Test
    fun usesScientificNotationForExtremeValues() {
        assertEquals("5E-33", 0.000000000000000000000000000000005.axis())
        assertEquals("5E5", 500000.0.axis())
        assertEquals("-5E5", (-500000.0).axis())
    }

    @Test
    fun respectsSpringLocaleContext() {
        LocaleContextHolder.setLocale(Locale.of("nb", "NO"))

        assertEquals("2,1", 2.100.axis())
        assertEquals("12,34", 12.3400.axis())
    }

    @Test
    fun handlesZero() {
        assertEquals("0", 0.0.axis())
    }
}