package com.techreier.edrops.graph

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class NiceNumberTest {

    @Test
    fun tooLowTest() {
        assertThrows<IllegalStateException> {
            niceNumber(0.9)
        }
    }

    @Test
    fun tooHighTest() {
        assertThrows<IllegalStateException> {
            niceNumber(10.1)
        }
    }

    @Test
    fun normalLowTest() {
        val result = niceNumber(1.3)
        assertEquals(1.0, result.result, TOLERANCE)
    }

    @Test
    fun middleTest() {
        val result = niceNumber(5.05)
        assertEquals(5.0, result.result, TOLERANCE)

    }

    @Test
    fun normalHighTest() {
        val result = niceNumber(9.8)
        assertEquals(10.0, result.result, TOLERANCE)
    }
}