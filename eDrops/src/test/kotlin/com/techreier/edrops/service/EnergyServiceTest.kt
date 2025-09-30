package com.techreier.edrops.service

import com.techreier.edrops.model.EnergyValues
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.assertj.core.api.Assertions.within
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

private const val DELTA = 0.01

class EnergyServiceTest {

    private val service = EnergyService()
    private val ep2024 = service.energyYears[2024]

    @Test
    fun energyTest() {
        assertEquals(139.984, value(EnergySource.WATER).twh ?: 0.0, 0.001)
        assertEquals(54, service.energyYears.count())
        assertNotNull(ep2024)
        assertEnergy(EnergySource.WATER, orig = 139.984, twh = 139.984, tj = 503.9424)
        assertEnergy(EnergySource.WIND, orig = 14.545, twh = 14.545, tj = 52.362)
        assertEnergy(EnergySource.SOLAR, orig = 0.251, twh = 0.251, tj = 0.9036)
        assertEnergy(EnergySource.EL_PROD, orig = 157.14, twh = 157.14,tj =  565.7)
        assertEnergy(EnergySource.HEAT, orig = 2.357, twh = 2.357, tj = 8.4852)
        assertEnergy(EnergySource.GAS, orig = 126.24, twh = 674.82, tj = 4858.71)
        assertEnergy(EnergySource.OIL, orig = 116.36, twh = 460.29, tj = 4478.54)
    }

    private fun assertEnergy(
        source: EnergySource,
        orig: Double? = null,
        twh: Double? = null,
        tj: Double? = null,
    ) {
        twh?.let { assertThat(value(source).twh).isCloseTo(it, within(DELTA)) }
        tj?.let { assertThat(value(source).tj).isCloseTo(it, within(DELTA)) }
        orig?.let { assertThat(value(source).orig).isCloseTo(it, within(DELTA)) }
    }

    private fun value(source: EnergySource): EnergyValues =
        ep2024?.find { e -> e.source == source } ?: fail("Could not find source ${source}")

}