package com.walcart.bff;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class BffApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(BffApiApplication.class, args);
	}

}
