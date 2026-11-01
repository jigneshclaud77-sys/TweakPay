package com.example.demo;

import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAsync 
public class TweakPayApplication {

	public static void main(String[] args) {
		SpringApplication.run(TweakPayApplication.class, args);
	}

}
