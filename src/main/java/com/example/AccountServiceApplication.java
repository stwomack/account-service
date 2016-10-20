package com.example;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Collection;
import java.util.stream.Stream;

@RepositoryRestResource
interface AccountRepository extends PagingAndSortingRepository<Account, Long> {

    @RestResource(path = "by-name")
    Collection<Account> findByAccountName(@Param("accountName") String accountName);

}

@SpringBootApplication
@EnableAutoConfiguration
@EnableDiscoveryClient
//@EnableJpaRepositories // <---- Add this
//@Import(RepositoryRestMvcConfiguration.class) // <---- And this
public class AccountServiceApplication {

    @Value("${myQuery}")
    private String query;

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

@RefreshScope
@RestController
class MessageRestController {

    @Value("${message}")
    private String message;

    @RequestMapping("/message")
    String getMessage() {
        System.out.println("MESSAGE CALLED");
        return this.message;
    }

    @HystrixCommand(fallbackMethod = "reliable")
    public String getCircuit() {
        return "This is succeeding";
    }

    public String reliable() {
        return "This is faling";
    }
}