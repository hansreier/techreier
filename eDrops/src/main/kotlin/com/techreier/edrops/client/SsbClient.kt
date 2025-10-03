package com.techreier.edrops.client

import com.techreier.edrops.model.ssb.request.QueryFilter
import com.techreier.edrops.model.ssb.request.ResponseFormat
import com.techreier.edrops.model.ssb.request.Selection
import com.techreier.edrops.model.ssb.request.SsbQueryRequest
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

// https://www.ssb.no/statbank/table/11561
// https://www.ssb.no/energi-og-industri/energi/statistikk/produksjon-og-forbruk-av-energi-energibalanse-og-energiregnskap

@Component
class SsbClient(private val restClient: RestClient) {

    companion object {
        const val EBO1_PRODUCTION="EB01"
        const val EBO2_IMPORT="EB02"
        const val EBO3_EXPORT="EB03"
        const val EBO6_NET_NORWAY="EB06"
        val energyList = listOf(EBO1_PRODUCTION, EBO2_IMPORT, EBO3_EXPORT, EBO6_NET_NORWAY)

    }


    fun fetchEnergyData2(): String {
        val jsonQuery = SsbQueryRequest(
            query = listOf(
                QueryFilter(
                    code = "Energibalanse",
                    selection = Selection(
                        filter = "vs:Energibalansepost01",
                        values = energyList
                    )
                ),
                QueryFilter(
                    code = "EnergiProdukt",
                    selection = Selection(
                        filter = "vs:EnergiProdukt",
                        values = listOf("EP03", "EP04IF", "EP07", "EP081_84")
                    )
                )
            ),
            response = ResponseFormat(format = "json-stat2")
        )

        return restClient.post()
            .uri("https://data.ssb.no/api/v0/no/table/11561/")
            .contentType(MediaType.APPLICATION_JSON)
            .body(jsonQuery)
            .retrieve()
            .body(String::class.java) ?: ""

    }

    fun fetchEnergyData(): String {
        val jsonQuery = """
       {
  "query": [
    {
      "code": "Energibalanse",
      "selection": {
        "filter": "vs:Energibalansepost01",
        "values": [
          "EB01",
          "EB02",
          "EB03",
          "EB06"
        ]
      }
    },
    {
      "code": "EnergiProdukt",
      "selection": {
        "filter": "vs:EnergiProdukt",
        "values": [
          "EP03",
          "EP04IF",
          "EP07",
          "EP081_84"
        ]
      }
    }
  ],
  "response": {
    "format": "json-stat2"
  }
}
        """.trimIndent()

        return restClient.post()
            .uri("https://data.ssb.no/api/v0/no/table/11561/")
            .contentType(MediaType.APPLICATION_JSON)
            .body(jsonQuery)
            .retrieve()
            .body(String::class.java) ?: ""
    }
}
