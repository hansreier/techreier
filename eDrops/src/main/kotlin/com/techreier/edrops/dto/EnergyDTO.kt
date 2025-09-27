package com.techreier.edrops.dto


import com.techreier.edrops.model.EnergyValues
import com.techreier.edrops.util.fixed
import org.springframework.context.MessageSource

data class EnergyDTO(val source: String, val value: String, val unit: String, val twh: String, val pj: String)

fun MutableList<EnergyValues>.toDTOs(m: MessageSource): List<EnergyDTO> =
    this.map { energyValues ->
        EnergyDTO(
            energyValues.source.name(m),
            energyValues.orig.fixed(),
            energyValues.source.energyUnit,
            energyValues.twh.fixed(),
            energyValues.tj.fixed(1)
        )
    }
