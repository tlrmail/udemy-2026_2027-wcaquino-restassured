package br.ce.wcaquino.rest;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class UserJsonTest {

	private String uri ;

	@BeforeEach
	void setUp() {
		uri = "http://restapi.wcaquino.me/users";
	}

	@Test
	public void deveVerificarPrimeiroNivel() {
		RestAssured
			.given()
			.when()
				.get(uri.concat("/1"))
			.then()
				.statusCode(200)
				.body("id", is(1))
				.body("name", containsString("João"))
				.body("age", Matchers.greaterThan(18))
				.body("salary", isA(Float.class))
				.body("salary", greaterThan(1000f))
			;
	}
	
	@Test
	public void deveVerificarPrimeiroNivelQuandoTiverOutrasFormas() {
		Response response = RestAssured.request(Method.GET, uri.concat("/1"));
		
		//path >> 1ª forma
		String id =	response.path("id").toString();
		System.out.println(id);

		Integer valorEsperado = 1;
		
		Assertions.assertEquals(valorEsperado, Integer.valueOf(id));
		Assertions.assertEquals(valorEsperado, response.path("%s", "id"));
		
		//jsonpath >> 2ª forma
		JsonPath jpath = new JsonPath(response.asString());
		Assertions.assertEquals(1, jpath.getInt("id"));
		Assertions.assertEquals("João da Silva", jpath.getString("name"));
		Assertions.assertEquals(30, jpath.getInt("age"));
		Assertions.assertEquals(1234.5678F, jpath.getFloat("salary"));
		
		//from >> 3ª forma
		int identificador = JsonPath.from(response.asString()).getInt("id");
		Assertions.assertEquals(valorEsperado, identificador);
	}
	
	@Test
	public void deveVerificarOBodyQuandoForOSegundoNivel() {
		RestAssured
			.given() // Cenário :: pré-requisito
			.when() // Execução
				.get(uri.concat("/2"))
			.then() // Verificação
				.statusCode(200)
				.body("name", containsString("Joaquina"))
				.body("age", greaterThan(18))
				.body("endereco.rua", is("Rua dos bobos")) // Segundo nível
			;
	}

	@Test
	public void deveVerificarUmaLista() {
		RestAssured
			.given() // Cenário :: pré-requisito
			.when() // Execução
				.get(uri.concat("/3"))
			.then() // Verificação
				.statusCode(200)
				.body("name", containsString("Ana"))
				.body("filhos", hasSize(2))
				.body("filhos.name", hasItem("Luizinho"))
				.body("filhos.name", hasItems("Zezinho", "Luizinho"))
				.body("filhos[0].name", is("Zezinho"))
				.body("filhos[1].name", is("Luizinho"))
			;
	}
	
	@Test
	public void deveLancarMensagemDeErroQuandoUsuarioInexistente() {
		RestAssured
			.given()
			.when()
				.get(uri.concat("/4"))
			.then()
				.statusCode(404)
				.body("error", is("Usuário inexistente"))
			;
	}

	@Test
	public void deveVerificarListaNaRaiz() {
		RestAssured
			.given()
			.when()
				.get(uri)
			.then()
				.statusCode(200)
				.body("", hasSize(3))
				.body("$", hasSize(3))
				.body("name", hasItems("João da Silva", "Maria Joaquina", "Ana Júlia"))
				.body("age[1]", is(25))
				.body("filhos.name", hasItem(Arrays.asList("Zezinho", "Luizinho")))
				.body("salary", contains(1234.5678f ,2500, null))
			;
	}
	
	@Test
	public void deveFazerVerificacoesAvancadasQuandoUsarGroovy() {
		RestAssured
		.given()
		.when()
			.get(uri)
		.then()
			.statusCode(200)
			.body("$", hasSize(3))
			.body("age.findAll{it <= 25}.size()", is(2))
			.body("age.findAll{it <= 25 && it > 20}.size()", is(1))
			.body("findAll{it.age <= 25 && it.age > 20}.name", hasItem("Maria Joaquina"))
			.body("findAll{it.age <= 25 && it.age > 20}[0].name", is("Maria Joaquina"))
			.body("findAll{it.age <= 25 }[-1].name", is("Ana Júlia")) // [-1] implica dizer que é o último
			.body("find{it.age <= 25}.name", is("Maria Joaquina"))
			.body("findAll{it.name.contains('n')}.name", hasItems("Maria Joaquina", "Ana Júlia"))
			.body("findAll{it.name.length() > 10}.name", hasItems("Maria Joaquina", "João da Silva"))
			.body("name.findAll{it.startsWith('Maria')}.collect{it.toUpperCase()}", hasItem("MARIA JOAQUINA"))
			.body("name.findAll{it.startsWith('Maria')}.collect{it.toUpperCase()}", 
					allOf(hasItem("MARIA JOAQUINA")))
			.body("age.collect{it * 2}", hasItems(60, 50, 40))
			.body("id.max()", is(3))
			.body("salary.min()", is(1234.5678f))
			.body("salary.findAll{it != null}.sum()", is(closeTo(3734.5678f, 0.001)))
			.body("salary.findAll{it != null}.sum()", allOf(greaterThan(3000d), lessThan(5000d)))
		;
	}
	
	@Test
	public void deveUnirJsonPathComJAVA() {
		ArrayList<String> names = 
		RestAssured
			.given()
			.when()
				.get(uri)
			.then()
				.statusCode(200)
				.extract()
				.path("name.findAll{it.startsWith('Maria')}")
		;
		
		Assertions.assertEquals(1, names.size());
		Assertions.assertTrue(names.get(0).equalsIgnoreCase("maria joaquina"));
				
	}
	
}
