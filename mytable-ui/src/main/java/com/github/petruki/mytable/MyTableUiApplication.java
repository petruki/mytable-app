package com.github.petruki.mytable;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication 
@EnableAutoConfiguration
@ComponentScan(basePackages={"com.github.petruki.mytable"})
@EntityScan(basePackages="com.github.petruki.mytable.model")
public class MyTableUiApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyTableUiApplication.class, args);
	}

}