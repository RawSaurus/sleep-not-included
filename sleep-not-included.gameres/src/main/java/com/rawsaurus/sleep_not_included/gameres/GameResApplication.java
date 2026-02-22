package com.rawsaurus.sleep_not_included.gameres;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@EnableFeignClients
@SpringBootApplication
public class GameResApplication {

	public static void main(String[] args) {
		SpringApplication.run(GameResApplication.class, args);
	}

}
