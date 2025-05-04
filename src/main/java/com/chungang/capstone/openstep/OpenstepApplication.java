package com.chungang.capstone.openstep;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
@EnableJpaAuditing
public class OpenstepApplication {

	public static void main(String[] args) {
		SpringApplication.run(OpenstepApplication.class, args);
		System.out.println("[Initiate Capstone Project OpenStep]");
	}

}
