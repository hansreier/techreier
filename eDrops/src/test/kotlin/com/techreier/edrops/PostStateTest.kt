package com.techreier.edrops

import com.techreier.edrops.domain.PostState
import com.techreier.edrops.exceptions.StateNotFoundException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class PostStateTest {


    @Test
    fun findLowercaseInput() {
        assertEquals(PostState.PUBLISHED, PostState.find("published", false))
    }

    @Test
    fun findMixedCaseWithFlag() {
        assertEquals(PostState.PUBLISHED, PostState.find("pUbLishEd", true))
    }

    @Test
    fun findInvalidInput() {
        assertThrows<StateNotFoundException> {
            PostState.find("soeppel", false)
        }
    }

    @Test
    fun findInvalidCase() {
        assertThrows<StateNotFoundException> {
            PostState.find("PUBLISHED", false)
        }
    }

    @Test
    fun findUnknownWithInvalidInputFromDatabase() {
        assertEquals(PostState.UNKNOWN, PostState.find("soeppel", true))
    }

    @Test
    fun findNullInput() {
        assertEquals(PostState.UNKNOWN, PostState.find(null, true))
    }

    @Test
    fun findEmptyInput() {
        assertEquals(PostState.UNKNOWN, PostState.find("", true))
    }
}