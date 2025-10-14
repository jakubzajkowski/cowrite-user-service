package com.example.cowrite;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class CowriteApplication {

	public static void main(String[] args) {
		SpringApplication.run(CowriteApplication.class, args);
	}

}
