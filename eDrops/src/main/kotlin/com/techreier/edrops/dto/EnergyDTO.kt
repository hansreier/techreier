package com.techreier.edrops.dto

import com.techreier.edrops.model.EnergyProduction
import com.techreier.edrops.service.EnergySource.*
import com.techreier.edrops.util.fixed
import com.techreier.edrops.util.twh
import org.springframework.context.MessageSource

data class EnergyDTO(val source: String, val value: String, val unit: String, val twh: String)

fun EnergyProduction.toDTO(m: MessageSource): List<EnergyDTO> {
    val water = EnergyDTO(WATER.name(m), this.water.twh(), WATER.energyUnit, this.water.twh())
    val wind = EnergyDTO(WIND.name(m), this.wind.twh(), WIND.energyUnit, this.wind.twh())
    val solar = EnergyDTO(SOLAR.name(m), this.solar.twh(), SOLAR.energyUnit, this.solar.twh())
    val heat = EnergyDTO(HEAT.name(m), this.heat.twh(), HEAT.energyUnit, this.heat.twh())
    val oil = EnergyDTO(OIL.name(m), this.oil.fixed(), OIL.energyUnit, this.oilToEl.twh())
    val gas = EnergyDTO(GAS.name(m), this.gas.fixed(), GAS.energyUnit, this.gasToEl.twh())

    val energyData = listOf(water, wind, solar, heat, oil, gas)
    return energyData

}