package com.techreier.edrops.endpoints

import com.techreier.edrops.config.logger
import kotlinx.coroutines.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestClient

@RestController
@RequestMapping("/api")
class TestController(val restClient: RestClient) {
    @GetMapping("/ping")
    fun ping(): String {
        logger.info("pong")
        return "pong"
    }

    @GetMapping("/block/{seconds}/{count}")
    fun block(@PathVariable seconds: Int, @PathVariable count: Int): String {
        logger.info("block endpoint")
        runBlocking {
            multipleRestCalls(count, seconds)
        }
        logger.info("Finished processing requests")
        return "Finished processing requests"
    }

    suspend fun multipleRestCalls(count: Int, seconds: Int) {
        val deferredResults = mutableListOf<Deferred<ResponseEntity<String>>>()

        coroutineScope {
            // Launch multiple coroutines concurrently
            repeat(count) { index ->
                val deferred = async(Dispatchers.IO) { //needs this dispatcher for IO tasks
                    restClient.get()
                        .uri("http://localhost:8080/api/wait/$seconds/$index")
                        .retrieve()
                        .toEntity(String::class.java)
                }
                deferredResults.add(deferred)
            }
        }
        logger.info("all Rest call started")
        // Waiting for all coroutines to complete
        val results = deferredResults.map { it.await() }
        results.forEach { result ->
            logger.info("Received response ${result.statusCode}, ${result.body})")
        }
    }


}