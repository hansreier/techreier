package com.techreier.edrops.dto

import com.techreier.edrops.model.EnergyProduction
import com.techreier.edrops.service.EnergySource.*
import com.techreier.edrops.util.fixed
import com.techreier.edrops.util.twh
import org.springframework.context.MessageSource

data class EnergyDTO(val source: String, val value: String, val unit: String, val twh: String)

fun EnergyProduction.toDTO(m: MessageSource): List<EnergyDTO> {
    val water = EnergyDTO(WATER.name(m), this.waterTWh.twh(), WATER.energyUnit, this.waterTWh.twh())
    val wind = EnergyDTO(WIND.name(m), this.windTWh.twh(), WIND.energyUnit, this.windTWh.twh())
    val solar = EnergyDTO(SOLAR.name(m), this.solarTWh.twh(), SOLAR.energyUnit, this.solarTWh.twh())
    val heat = EnergyDTO(HEAT.name(m), this.heatTWh.twh(), HEAT.energyUnit, this.heatTWh.twh())
    val oil = EnergyDTO(OIL.name(m), this.oilSm3.fixed(), OIL.energyUnit, this.oilTWh.twh())
    val gas = EnergyDTO(GAS.name(m), this.gasSm3.fixed(), GAS.energyUnit, this.gasTWh.twh())

    val energyData = listOf(water, wind, solar, heat, oil, gas)
    return energyData

}