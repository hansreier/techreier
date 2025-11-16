package com.techreier.edrops.controllers

import jakarta.servlet.http.HttpSession
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.time.ZoneId
import org.springframework.web.bind.annotation.RequestMapping

@RestController
@RequestMapping("timezone")
class TimezoneController {

    //Receive time zone from client
    @PostMapping
    fun setTimezone(@RequestBody body: Map<String, String>, session: HttpSession) {
        val timezone = body["timezone"]
        timezone?.let {
            session.setAttribute("timezone", ZoneId.of(it))
        }
    }
}