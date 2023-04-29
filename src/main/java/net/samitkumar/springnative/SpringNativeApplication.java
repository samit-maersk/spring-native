package net.samitkumar.springnative;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class SpringNativeApplication {

	@Value("${spring.application.jsonplaceholder.host}")
	private String jsonplaceholderHost;

	public static void main(String[] args) {
		SpringApplication.run(SpringNativeApplication.class, args);
	}

	@Bean
	RouterFunction routes() {
		return RouterFunctions.route()
				.path("/person", builder -> builder
						.GET("/world", request -> ServerResponse.ok().body(Mono.just("Hello World"), String.class))
						.GET("/samit", request -> ServerResponse.ok().body(Mono.just("Hello Samit"), String.class))
				).build();
	}

	@Bean
	UserClient userClient(WebClient.Builder builder) {
		var httpServiceProxyFactory = HttpServiceProxyFactory
				.builder(WebClientAdapter.forClient(builder.baseUrl(jsonplaceholderHost).build()))
				.build();
		return httpServiceProxyFactory.createClient(UserClient.class);
	}

}

record Address(String street, String suite, String city, String zipcode, Geo geo) {}
record Geo(String lat, String lng) {}
record Company(String name, String catchPhrase, String bs) {}
record User(int id, String name, String username, String email, String phone, String website, Address address, Company company) {}
@HttpExchange(url = "/users", accept = "application/json", contentType = "application/json")
interface UserClient {
	@GetExchange
	Flux<User> getUsers();

	@GetExchange("/{id}")
	Mono<User> getUserbyId(@PathVariable("id") int id);

}