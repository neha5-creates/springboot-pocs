package com.poc.orderprocessing;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableScheduling
public class OrderProcessingPocApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderProcessingPocApplication.class, args);
	}

}
