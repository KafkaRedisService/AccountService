package com.example.accountservice;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

@SpringBootApplication
@EnableScheduling
//@EnableOAuth2Sso
@RestController
public class AccountserviceApplication {


	public static void main(String[] args) {
		SpringApplication.run(AccountserviceApplication.class, args);
	}
	
	@Bean
	NewTopic notification() {
		//topic name, partition number, replication number
		return new NewTopic("notification", 2, (short) 3);
	}
	
	@Bean
	NewTopic statistic() {
		//topic name, partition numbers, replication number
		return new NewTopic("statistic", 1, (short) 3);
	}

}
