package com.orange.credicard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class CredicardApplication {

	public static void main(String[] args) {
		SpringApplication.run(CredicardApplication.class, args);
	}

}
