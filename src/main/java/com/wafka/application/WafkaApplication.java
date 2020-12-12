package com.wafka.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.wafka")
@EnableAutoConfiguration
public class WafkaApplication {
	public static void main(String[] args) {
		SpringApplication.run(WafkaApplication.class, args);
	}
}
