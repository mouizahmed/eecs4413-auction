package com.teamAgile.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.GetMapping;

@SpringBootApplication
@EnableScheduling
public class AuctionApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuctionApplication.class, args);
	}

}
