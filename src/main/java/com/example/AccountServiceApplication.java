package com.example;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.stream.Stream;

@SpringBootApplication
//@EnableAutoConfiguration
//@EnableDiscoveryClient
public class AccountServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AccountServiceApplication.class, args);
	}

	@Bean
	CommandLineRunner dummyCLR(AccountRepository reservationRepository) {
		return args -> {
			Stream.of("Womack", "Dostoyevsky", "Rockefeller", "Ghandi")
					.forEach(name -> reservationRepository.save(new Account(name, "checking")));
			reservationRepository.findAll().forEach(System.out::println);
		};
	}
}

@RepositoryRestResource
interface AccountRepository extends PagingAndSortingRepository<Account, Long> {
	@RestResource(path = "by-name")
	Collection<Account> findByAccountName(@Param("accountName") String accountName);
}

@RefreshScope
@RestController
class MessageRestController {

	@Value("${foo}")
	private String message;

	@RequestMapping("/foo")
	String getMessage() {
		return this.message;
	}
}