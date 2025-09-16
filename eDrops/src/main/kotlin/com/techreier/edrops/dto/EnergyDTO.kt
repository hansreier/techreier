package com.techreier.edrops.dto

import com.techreier.edrops.model.EnergyProduction
import com.techreier.edrops.service.EnergySource.*
import com.techreier.edrops.util.fixed
import org.springframework.context.MessageSource

data class EnergyDTO(val source: String, val value: String, val unit: String, val twh: String, val tj: String)

fun EnergyProduction.toDTO(m: MessageSource): List<EnergyDTO> {
    val water =
        EnergyDTO(
            WATER.name(m), this.waterTWh.fixed(), WATER.energyUnit,
            this.waterTWh.fixed(), this.waterTJ.fixed(1)
        )

    val wind = EnergyDTO(
        WIND.name(m), this.windTWh.fixed(), WIND.energyUnit,
        this.windTWh.fixed(), this.windTJ.fixed(1)
    )
    val solar = EnergyDTO(
        SOLAR.name(m), this.solarTWh.fixed(), SOLAR.energyUnit,
        this.solarTWh.fixed(), this.solarTJ.fixed(1)
    )

    val heat = EnergyDTO(
        HEAT.name(m), this.heatTWh.fixed(), HEAT.energyUnit,
        this.heatTWh.fixed(), this.heatTJ.fixed(1)
    )

    val oil = EnergyDTO(
        OIL.name(m), this.oilMSm3.fixed(), OIL.energyUnit,
        this.oilTWh.fixed(), this.oilTJ.fixed(1)
    )

    val gas = EnergyDTO(
        GAS.name(m), this.gasMSm3.fixed(), GAS.energyUnit,
        this.gasTWh.fixed(), this.gasTJ.fixed(1)
    )

    val energyData = listOf(water, wind, solar, heat, oil, gas)
    return energyData

}