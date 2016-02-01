package com.example;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.core.MessageSource;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Stream;

@SpringBootApplication
@EnableBinding
@Configuration
@EnableAutoConfiguration
public class AccountServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AccountServiceApplication.class, args);
	}

	@Bean
	CommandLineRunner dummyCLR(AccountRepository reservationRepository) {
		return args -> {
			Stream.of("Womack", "Smith", "Dostoyevsky", "Caesar", "Obama", "Rockefeller")
					.forEach(name -> reservationRepository.save(new Account(name, "checking")));
			reservationRepository.findAll().forEach(System.out::println);
		};
	}
}

@RepositoryRestResource
interface AccountRepository extends JpaRepository<Account, Long> {
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