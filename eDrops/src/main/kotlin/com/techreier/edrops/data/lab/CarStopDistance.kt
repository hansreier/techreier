package com.techreier.edrops.data.lab

import com.techreier.edrops.data.EN
import com.techreier.edrops.data.NB
import com.techreier.edrops.util.timestamp

object CarStopDistance : IExpression {
    val timestamp = timestamp("17.05.2025 07:00:00")
    const val SEGMENT = "carstopdistance"
    const val SUBJECT_NO = "Bil bremselengde"
    const val SUBJECT_EN = "Car stopping distance"
    const val POS = 1

    val ABOUT_NB = """
        Beregner reaksjonslengde, bremselengde og total stopplengde for en bil (i meter).  

        - Juster **speed_kmh** (km/t), **reaction_time** (sekunder) og **friction**.
        - Typisk friksjon: **0.8** (tørr asfalt), **0.5** (våt asfalt), **0.25** (snø) og **0.15** (is).
    """.trimIndent()

    val ABOUT_EN = """
        Calculates reaction distance, braking distance, and total stopping distance for a car (in meters).  

         - Adjust **speed_kmh** (km/h), **reaction_time** (seconds), and **friction**.
         - Typical friction: **0.8** (dry asphalt), **0.5** (wet asphalt), **0.25** (snow), and **0.15** (ice).
    """.trimIndent()

    val EXPR = """
        speed[kmh] = 80;
        reaction_time[s] = 1.0;
        friction = 0.25;
        g[ms2] = 9.81;
        v[ms] = speed[kmh] / 3.6;
        reaction_dist[m] = v[ms] * reaction_time[s];
        brake_dist[m] = (v[ms]^2) / (2 * friction * g[ms2]);
        stop_dist[m] = reaction_dist[m] + brake_dist[m]
""".trimIndent()

    override fun no() = Expression(timestamp, SEGMENT, POS, SUBJECT_NO, ABOUT_NB, EXPR, NB)
    override fun en() = Expression(timestamp, SEGMENT, POS, SUBJECT_EN, ABOUT_EN, EXPR, EN)
}