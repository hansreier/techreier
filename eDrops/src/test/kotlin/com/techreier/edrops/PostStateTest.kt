package com.techreier.edrops

import com.techreier.edrops.domain.PostState
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class PostStateTest {

    @Test
    fun findExactMatch() {
        assertEquals(PostState.PUBLISHED, PostState.find("PUBLISHED"))
    }

    @Test
    fun findLowercaseInput() {
        assertEquals(PostState.PUBLISHED, PostState.find("published"))
    }

    @Test
    fun findMixedCaseWithFlag() {
        assertEquals(PostState.PUBLISHED, PostState.find("pUbLishEd", true))
    }

    @Test
    fun findInvalidInput() {
        assertEquals(PostState.UNKNOWN, PostState.find("SOEPPEL"))
    }

    @Test
    fun findNullInput() {
        assertEquals(PostState.UNKNOWN, PostState.find(null))
    }

    @Test
    fun findEmptyInput() {
        assertEquals(PostState.UNKNOWN, PostState.find(""))
    }
}