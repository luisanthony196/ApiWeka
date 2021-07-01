package com.bytecode.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class DemoApplication {
	public static void main (String[] args) {
		ConfigurableApplicationContext c = SpringApplication.run(DemoApplication.class, args);

		System.out.println("You aren't welcome to Spring Boot");
	}
}