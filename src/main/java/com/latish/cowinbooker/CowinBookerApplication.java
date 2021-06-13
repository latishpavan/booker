package com.latish.cowinbooker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class CowinBookerApplication {

	public static void main(String[] args) {
		SpringApplication.run(CowinBookerApplication.class, args);
	}

}
