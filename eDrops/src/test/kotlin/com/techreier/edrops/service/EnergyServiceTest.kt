package com.techreier.edrops.service

import com.techreier.edrops.model.EnergyValues
import org.assertj.core.api.Assertions.fail
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class EnergyServiceTest {

    private val service = EnergyService()
    private val ep2024 = service.energyYears[2024]

    @Test
    fun energyTest() {
        assertEquals(54, service.energyYears.count())
        assertNotNull(ep2024)
        assertEquals(139.984, value(EnergySource.WATER).twh ?: 0.0, 0.001)
        assertEquals(503.9424, value(EnergySource.WATER).tj ?: 0.0, 0.001)
        assertEquals(14.545, value(EnergySource.WIND).twh ?: 0.0, 0.001)
        assertEquals(52.362, value(EnergySource.WIND).tj ?: 0.0, 0.001)
        assertEquals(0.251, value(EnergySource.SOLAR).twh ?: 0.0, 0.001)
        assertEquals(0.9036, value(EnergySource.SOLAR).tj ?: 0.0, 0.001)
        assertEquals(2.357, value(EnergySource.HEAT).twh ?: 0.0, 0.001)
        assertEquals(8.4852, value(EnergySource.HEAT).tj ?: 0.0, 0.001)
        assertEquals(116.36, value(EnergySource.OIL).orig ?: 0.0, 0.001)
        assertEquals(1657.3243, value(EnergySource.OIL).twh ?: 0.0, 0.001)
        assertEquals(16125.317879, value(EnergySource.OIL).tj ?: 0.0, 0.001)
        assertEquals(126.23753, value(EnergySource.GAS).orig ?: 0.0, 0.001)
        assertEquals(2429.7444, value(EnergySource.GAS).twh ?: 0.0, 0.001)
        assertEquals(17494.160292, value(EnergySource.GAS).tj ?: 0.0, 0.001)
    }

    private fun value(source: EnergySource): EnergyValues =
        ep2024?.find { e -> e.source == source } ?: fail("Could not find source ${source}")

}