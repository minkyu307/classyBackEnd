package com.prototype.classyBackEnd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class ClassyBackEndApplication {

	public static final String APPLICATION_LOCATIONS = "spring.config.location="
			+ "classpath:application.properties,"
			+ "classpath:aws.yml";

	public static void main(String[] args) {
		SpringApplication.run(ClassyBackEndApplication.class, args);
		//new SpringApplicationBuilder(ClassyBackEndApplication.class).properties(APPLICATION_LOCATIONS).run(args);
	}

}
