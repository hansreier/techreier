package com.techreier.edrops.dto

import com.techreier.edrops.model.EnergyProduction
import com.techreier.edrops.service.EnergySource.*
import com.techreier.edrops.util.fixed
import com.techreier.edrops.util.twh

data class EnergyDTO(val source: String, val value: String, val unit: String, val twh: String)

fun EnergyProduction.toDTO(): List<EnergyDTO> {

    val water = EnergyDTO(WATER.key, this.water.twh(), WATER.energyUnit, this.water.twh())
    val wind = EnergyDTO(WIND.key, this.wind.twh(), WIND.energyUnit, this.wind.twh())
    val solar = EnergyDTO(SOLAR.key, this.solar.twh(), SOLAR.energyUnit, this.solar.twh())
    val heat = EnergyDTO(HEAT.key, this.heat.twh(), HEAT.energyUnit, this.heat.twh())
    val oil = EnergyDTO(OIL.key, this.oil.fixed(), OIL.energyUnit, this.oilToEl.twh())
    val gas = EnergyDTO(NATURAL_GAS.key, this.gas.fixed(), NATURAL_GAS.energyUnit, this.gasToEl.twh())

    val energyData = listOf(water, wind, solar, heat, oil, gas)
    return energyData

}