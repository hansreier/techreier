package com.techreier.edrops.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class EnergyServiceTest {

    private val energyService = EnergyService()

    @Test
    fun elTest() {
        energyService.readElExcel()
        assertEquals(17, energyService.energyProduction.size)
    }

    @Test
    fun fossilTest() {
        energyService.readFossilExcel()
        assertEquals(54, energyService.energyProduction.size)
    }

    @Test
    fun energyTest() {
        energyService.readFossilExcel()
        energyService.readElExcel()
        assertEquals(54, energyService.energyProduction.size)
    }
}