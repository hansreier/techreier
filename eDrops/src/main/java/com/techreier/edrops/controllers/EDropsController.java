package com.techreier.edrops.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

//Note there is a difference between @Controller and @RestController
//@Restcontroller does not work when referencing to thymeleaf templates
//@RestController is for web services.
@Controller
@RequestMapping("/")
public class EDropsController {
	private static final Logger logger = LoggerFactory.getLogger(EDropsController.class);
	// inject via application.properties
	@Value("${welcome.message}")
	private String message = "Hello World";
	@GetMapping({ "/java", "/greeting" })
	public String welcome(Model model) {
		logger.info("Welcome to Java");
		model.addAttribute("message", this.message);
		model.addAttribute("message1", "from Java");
		return "welcome";
	}
}

