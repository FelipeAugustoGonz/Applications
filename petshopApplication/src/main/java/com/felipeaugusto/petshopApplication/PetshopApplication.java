package com.felipeaugusto.petshopApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(info = @Info(
		title = "PetShop MRM API",
		version = "1.0",
		description = "API para gerenciamento de categorias, produtos, carrinho e pedidos do PetShop MRM.",
		contact = @Contact(name = "PetShop MRM")
))
@SpringBootApplication
public class PetshopApplication {

	public static void main(String[] args) {
		SpringApplication.run(PetshopApplication.class, args);
	}

}
