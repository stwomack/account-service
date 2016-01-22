package com.example;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.Collection;
import java.util.stream.Stream;

@SpringBootApplication
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
