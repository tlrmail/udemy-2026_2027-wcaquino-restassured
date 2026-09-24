package br.ce.wcaquino.rest;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasXPath;
import static org.hamcrest.Matchers.is;

import java.util.ArrayList;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;

public class UserXMLTest {

	private String uri ;

	@BeforeEach
	void setUp() {
		uri = "https://restapi.wcaquino.me/usersXML";
	}

	@Test
	public void deveTrabalharComXML_1() {
		RestAssured
			.given()
			.when()
				.get(uri.concat("/3"))
			.then()
				.statusCode(200)
				.body("user.name", is("Ana Julia"))
				.body("user.@id", is("3"))
				.body("user.filhos.name.size()", is(2))
				.body("user.filhos.name[0]", is("Zezinho"))
				.body("user.filhos.name[1]", is("Luizinho"))
				.body("user.filhos.name", hasItem("Luizinho"))
				.body("user.filhos.name", hasItems("Luizinho", "Zezinho"))
			;
	}
	
	@Test
	public void deveTrabalharComXML_2() {
		RestAssured
			.given()
			.when()
				.get(uri.concat("/3"))
			.then()
				.statusCode(200)
				.rootPath("users")
				.body("name", is("Ana Julia"))
				.body("@id", is("3"))
				.rootPath("user.filhos")
				.body("name.size()", is(2))
				.body("name[0]", is("Zezinho"))
				.detachRootPath("filhos")
				.body("filhos.name[1]", is("Luizinho"))
				.rootPath("user.filhos")
				.body("name", hasItem("Luizinho"))
				.body("name", hasItems("Luizinho", "Zezinho"))
			;
	}

	@Test
	public void deveTrabalharComXML_3() {
		RestAssured
			.given()
			.when()
				.get(uri)
			.then()
				.statusCode(200)
				.body("users.user.size()", is(3))
				.body("users.user.findAll{it.age.toInteger() <= 25}.size()", is(2))
				.body("users.user.@id", hasItems("1", "2", "3"))
				.body("users.user.find{it.age == 25}.name", is("Maria Joaquina"))
				.body("users.user.findAll{it.name.toString().contains('n')}.name", hasItems("Maria Joaquina", "Ana Julia"))
				.body("users.user.salary.find{it != 1}", is("1234.5678"))
				.body("users.user.salary.find{it != 1}.toDouble()", is(1234.5678d))
				.body("users.user.age.collect{it.toInteger() * 2}", hasItems(40, 50, 60))
				.body("users.user.name.findAll{it.toString().startsWith('Maria')}.collect{it.toString().toUpperCase()}", is("MARIA JOAQUINA"))
			;
	}
	
	@Test
	public void deveFazerPesquisasAvancadasComXMLEJava_1() {
		
		String name = 	
			RestAssured
				.given()
				.when()
					.get(uri)
				.then()
					.statusCode(200)
					.extract().path("users.user.name.findAll{it.toString().startsWith('Maria')}")
				;
		
		Assertions.assertEquals("Maria Joaquina".toUpperCase(), name.toUpperCase());
		
		System.out.println(name);
	}
	
	@Test
	public void deveFazerPesquisasAvancadasComXMLEJava_2() {
		
		ArrayList<Object> nomes = 	
			RestAssured
				.given()
				.when()
					.get(uri)
				.then()
					.statusCode(200)
					.extract().path("users.user.name.findAll{it.toString().contains('n')}")
				;
		
//		Assertions.assertEquals("Maria Joaquina".toUpperCase(), path.toUpperCase());
		Assertions.assertEquals(2, nomes.size());
		Assertions.assertEquals("Maria Joaquina".toUpperCase(), nomes.get(0).toString().toUpperCase());
		Assertions.assertTrue("ANA JULIA".equalsIgnoreCase(nomes.get(1).toString()));
		System.out.println(nomes);
	}

	@Test
	public void deveFazerPesquisasAvancadasComXMLComXPath() {
		
		RestAssured
				.given()
				.when()
					.get(uri)
				.then()
					.statusCode(200)
					.body(hasXPath("count(/users/user)", is("3")))
					.body(hasXPath("/users/user[@id = '1']"))
					.body(hasXPath("//user[@id = '2']"))
					.body(hasXPath("//name[text() = 'Luizinho']"))
					.body(hasXPath("//user[@id = '3']//name", is("Ana Julia")))
					.body(hasXPath("//name[text() = 'Ana Julia']/following-sibling::filhos", allOf(containsString("Zezinho"), containsString("Luizinho"))))
					.body(hasXPath("/users/user[1]/name"), containsString("João da Silva"))
					.body(hasXPath("/users/user[last()]/name"), containsString("Ana Julia"))
					.body(hasXPath("/users/user[last()]/name"), containsString("Ana Julia"))
					.body(hasXPath("/users/user/name[contains(.,'n')]"), allOf(containsString("Ana Julia"), containsString("Maria Joaquina")))
//					.body(hasXPath("count(/users/user/name[contains(.,'n')])"), is("2"))
				;
		
	}
	
	
}
