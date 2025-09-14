package com.techreier.edrops.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class EnergyServiceTest {

    private val energyService = EnergyService()

    @Test
    fun energyTest() {
        assertEquals(54, energyService.energyProduction.size)
        val ep2024 = energyService.energyProduction[2024]
        assertNotNull(ep2024)
        assertEquals(139.984, ep2024!!.water!!, 0.001)
        assertEquals(14.545, ep2024.wind!!, 0.001)
        assertEquals(0.251, ep2024.solar!!, 0.001)
        assertEquals(2.357, ep2024.heat!!, 0.001)
        assertEquals(4479.2549, ep2024.oil!!, 0.001)
        assertEquals(1657.3243, ep2024.oilToEl!!, 0.001)
        assertEquals(4859.48897, ep2024.gas!!, 0.001)
        assertEquals(2429.7444, ep2024.gasToEl!!, 0.001)
    }
}