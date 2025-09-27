package com.techreier.edrops.client

import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
// https://www.ssb.no/statbank/table/11561
// https://www.ssb.no/energi-og-industri/energi/statistikk/produksjon-og-forbruk-av-energi-energibalanse-og-energiregnskap

@Component
class SsbClient(private val restClient: RestClient) {

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
