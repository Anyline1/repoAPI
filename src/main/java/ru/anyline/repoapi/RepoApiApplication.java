package ru.anyline.repoapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class RepoApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(RepoApiApplication.class, args);
	}

}
