package com.urgoringo.mealkit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MealkitApplication {

	public static void main(String[] args) {
		SpringApplication.run(MealkitApplication.class, args);
	}

}
