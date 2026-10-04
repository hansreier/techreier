package com.techreier.edrops.calc

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class CalcUtilTest {

    @Test
    fun parseDoubleTest() {
        val double = parseDouble("3.14")
        assertNotNull(double)
        assertThat(double).isInstanceOf(Double::class.javaObjectType)
        assertThat(double).isEqualTo(3.14)
    }

    @Test
    fun parseDoubleNorwegianTest() {
        val double = parseDouble("3,14")
        assertNotNull(double)
        assertThat(double).isInstanceOf(Double::class.javaObjectType)
        assertThat(double).isEqualTo(3.14)
    }

    @Test
    fun parseDoubleIntegerTest() {
        val double = parseDouble("3")
        assertNotNull(double)
        assertThat(double).isInstanceOf(Double::class.javaObjectType)
        assertThat(double).isEqualTo(3.0)
    }



}