package com.sigmondsmart.edrops;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Point to class with the @SpringBootApplication annotation and app arguments
 * 
 * main starts Servlet 3 style Java config API that replaces web.xml
 *
 */
@SpringBootApplication
public class EDropsApp extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(EDropsApp.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(EDropsApp.class, args);
	}
}
