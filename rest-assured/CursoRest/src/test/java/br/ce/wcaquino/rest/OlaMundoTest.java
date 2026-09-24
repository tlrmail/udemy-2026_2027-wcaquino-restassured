package br.ce.wcaquino.rest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.startsWith;

import java.util.Arrays;
import java.util.List;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;

public class OlaMundoTest {

	private String uri ;
	private Response response;

	@BeforeEach
	void setUp() {
		uri = "http://restapi.wcaquino.me:80/ola";
		response = RestAssured.request(Method.GET, uri);
	}
	
	@Test
	public void test01OlaMundo() {

		response = RestAssured.request(Method.GET, uri);
		ValidatableResponse validacao = response.then();
		
		validacao.statusCode(200);
		
		Assertions.assertEquals(response.getBody().asString(), "Ola Mundo!");
		Assertions.assertTrue(response.statusCode() == 200);
		Assertions.assertFalse(response.statusCode() == 201);

	}

	@Test
	public void deveConhecerOutrasFormasDeRestassured_1() {
		response = RestAssured.get(uri);
		ValidatableResponse validacao = response.then();
		validacao.statusCode(200);
	}
	
	@Test
	public void deveConhecerOutrasFormasDeRestassured_2() {
		RestAssured
			.get(uri)
			.then()
				.statusCode(200)
			;
	}

	@Test
	public void deveConhecerOutrasFormasDeRestassured_3() {
		RestAssured
			.given() //Pré-condições -> Cenário
			.when() // Ação de fato  -> Execução/Ação
				.get(uri)
			.then() // Verificações  -> Assertivas
				.statusCode(200)
			;
	}
	
	@Test
	public void devoConhecerMatchersHamcrest() {
		MatcherAssert.assertThat("Maria", Matchers.is("Maria"));
		MatcherAssert.assertThat(128, Matchers.is(128));
		MatcherAssert.assertThat(128, Matchers.isA(Integer.class));
		MatcherAssert.assertThat(128.86d, Matchers.isA(Double.class));
		MatcherAssert.assertThat(128.4382F, Matchers.isA(Float.class));
		MatcherAssert.assertThat(133782L, Matchers.isA(Long.class));
		MatcherAssert.assertThat(128, Matchers.greaterThan(35));
		MatcherAssert.assertThat(128, Matchers.lessThan(350));		
		
		List<Integer> impares = Arrays.asList(1,3,5,7,9);
		assertThat(impares, Matchers.hasSize(5));
		assertThat(impares, contains(1,3,5,7,9));
		assertThat(impares, containsInAnyOrder(3,1,9,7,5));
		assertThat(impares, hasItems(3,5,7));
		assertThat(impares, hasItem(5));
		
		assertThat("Maria", is(not("João")));
		assertThat("Maria", not("João"));
		assertThat("Maria", anyOf(is("Maria"), is("Joaquina"))); // Conectivo OU(v)
		assertThat("Luiz", allOf(startsWith("Lu"), endsWith("iz"), containsString("ui"))); // Conectivo E(^)
	}
	
	@Test
	public void devoValidarBody() {
		RestAssured
			.given()
			.when()
				.get(uri)
			.then()
				.statusCode(200)
				.body(is("Ola Mundo!"))
				.body(containsString("Mundo"))
				.body(notNullValue())
			;
	}
	
	
}
