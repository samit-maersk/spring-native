package net.samitkumar.springnative;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class SpringNativeApplicationTests {

	@Test
	void contextLoads() {
	}

}

@SpringBootTest
@AutoConfigureWireMock(port = 0)
class UserClientTests {
	@Autowired
	private UserClient userClient;

	@DynamicPropertySource
	static void registerPgProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.application.jsonplaceholder.host", () -> "http://localhost:${wiremock.server.port}");
	}

	@BeforeEach
	void setup() {
		stubFor(get(urlEqualTo("/users"))
				.willReturn(aResponse()
						.withHeader("Content-Type", "application/json")
						.withBody("""
								[
								    {
								        "id": 1,
								        "name": "Leanne Graham",
								        "username": "Bret",
								        "email": "Sincere@april.biz",
								        "address": {
								            "street": "Kulas Light",
								            "suite": "Apt. 556",
								            "city": "Gwenborough",
								            "zipcode": "92998-3874",
								            "geo": {
								                "lat": "-37.3159",
								                "lng": "81.1496"
								            }
								        },
								        "phone": "1-770-736-8031 x56442",
								        "website": "hildegard.org",
								        "company": {
								            "name": "Romaguera-Crona",
								            "catchPhrase": "Multi-layered client-server neural-net",
								            "bs": "harness real-time e-markets"
								        }
								    },
								    {
								        "id": 2,
								        "name": "Ervin Howell",
								        "username": "Antonette",
								        "email": "Shanna@melissa.tv",
								        "address": {
								            "street": "Victor Plains",
								            "suite": "Suite 879",
								            "city": "Wisokyburgh",
								            "zipcode": "90566-7771",
								            "geo": {
								                "lat": "-43.9509",
								                "lng": "-34.4618"
								            }
								        },
								        "phone": "010-692-6593 x09125",
								        "website": "anastasia.net",
								        "company": {
								            "name": "Deckow-Crist",
								            "catchPhrase": "Proactive didactic contingency",
								            "bs": "synergize scalable supply-chains"
								        }
								    }
								]
								""")));
	}

	@Test
	@DisplayName("Should return all users")
	void getUsers() {
		userClient.getUsers()
				.as(StepVerifier::create)
				.expectNextCount(2)
				.verifyComplete();
	}

	@Test
	@DisplayName("Should return user by id")
	void getUserbyId() {
		stubFor(get(urlEqualTo("/users/1"))
				.willReturn(aResponse()
						.withHeader("Content-Type", "application/json")
						.withBody("""
								{
									"id": 1,
									"name": "Leanne Graham",
									"username": "Bret",
									"email": "Sincere@april.biz",
									"address": {
										"street": "Kulas Light",
										"suite": "Apt. 556",
										"city": "Gwenborough",
										"zipcode": "92998-3874",
										"geo": {
											"lat": "-37.3159",
											"lng": "81.1496"
										}
									},
									"phone": "1-770-736-8031 x56442",
									"website": "hildegard.org",
									"company": {
										"name": "Romaguera-Crona",
										"catchPhrase": "Multi-layered client-server neural-net",
										"bs": "harness real-time e-markets"
									}
								}
								""")));

		userClient.getUserById(1)
				.as(StepVerifier::create)
				.consumeNextWith(user -> {
					assertEquals(1, user.id());
					assertEquals("Leanne Graham", user.name());
				}).verifyComplete();
	}
}

@SpringBootTest
@AutoConfigureWebTestClient(timeout = "10000")
class RouterTest {
	@Autowired
	WebTestClient webTestClient;

	@Test
	void personGetAllTest() {
		webTestClient.get()
				.uri("/person")
				.exchange()
				.expectStatus().isOk()
				.expectBody()
				.equals("Hello World");
	}

	@Test
	void personGetByIdTest() {
		webTestClient.get()
				.uri("/person/1")
				.exchange()
				.expectStatus()
				.isOk()
				.expectBody()
				.equals("Hello Samit");
	}
}