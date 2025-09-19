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
        assertEquals(139.984, ep2024!!.waterTWh!!, 0.001)
        assertEquals(503.9424, ep2024.waterTJ!!, 0.001)
        assertEquals(14.545, ep2024.windTWh!!, 0.001)
        assertEquals(52.362, ep2024.windTJ!!, 0.001)
        assertEquals(0.251, ep2024.solarTWh!!, 0.001)
        assertEquals(0.9036, ep2024.solarTJ!!, 0.001)
        assertEquals(2.357, ep2024.heatTWh!!, 0.001)
        assertEquals(8.4852, ep2024.heatTJ!!, 0.001)
        assertEquals(116.36, ep2024.oilMSm3!!, 0.001)
        assertEquals(1657.3243, ep2024.oilTWh!!, 0.001)
        assertEquals(16125.317879, ep2024.oilTJ!!, 0.001)
        assertEquals(126.23753, ep2024.gasMSm3!!, 0.001)
        assertEquals(2429.7444, ep2024.gasTWh!!, 0.001)
        assertEquals(17494.160292, ep2024.gasTJ!!, 0.001)
    }

}