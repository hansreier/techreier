package com.techreier.edrops.endpoints

import com.techreier.edrops.config.logger
import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.Async
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestClient
import java.util.concurrent.CompletableFuture


// This code is dependent of using virtual threads in Spring
@RestController
@RequestMapping("/api")
@Profile("h2")
class VirtualThreadsController(val restClient: RestClient) {

    @GetMapping("/blockvt/{seconds}/{count}")
    fun blockWithVirtualThreads(@PathVariable seconds: Int, @PathVariable count: Int): String {
        logger.info("block syncronous endpoint using virtual threads")
        processRequests(seconds, count)
        logger.info("Finished processing blocking request")
        return "Finished processing blocking request using virtual threads"
    }

    @GetMapping("/noblockvt/{seconds}/{count}")
    fun nonBlockWithVirtualThreads(@PathVariable seconds: Int, @PathVariable count: Int): CompletableFuture<String> {
        logger.info("non blocking asyncronous endpoint using virtual threads")
        return CompletableFuture.supplyAsync {
            processRequestsAsync(count, seconds)
            "Finished processing non blocking request using virtual threads"
        }
    }

    @Async
    fun processRequestsAsync(seconds: Int, count: Int): CompletableFuture<String> {
        return CompletableFuture.supplyAsync {
            processRequests(seconds, count)
            "Finished processing non blocking request using virtual threads"
        }
    }

    fun processRequests(seconds: Int, count: Int) {
        val tasks = mutableListOf<Thread>()

        repeat(count) { index ->
            val task = Thread.ofVirtual().start { // Launch virtual threads
                restClient.get()
                    .uri("http://localhost:8080/api/waitvt/$seconds/$index")
                    .retrieve()
                    .toEntity(String::class.java)
            }
            tasks.add(task)
        }

        // Wait for all virtual threads to complete
        tasks.forEach { it.join() }
        logger.info("finished processing all requests")
    }
}




