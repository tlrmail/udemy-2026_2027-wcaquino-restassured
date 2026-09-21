package br.ce.wcaquino.rest;

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
}
