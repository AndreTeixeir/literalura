package io.github.andreteixeir.literalura;

import io.github.andreteixeir.literalura.service.ApiConsumer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LiteraluraApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(LiteraluraApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// Create an instance of our API consumer.
		ApiConsumer consumer = new ApiConsumer();

		// Define the search URL for the Gutendex API.
		// We are searching for books by "Machado de Assis".
		// Spaces are replaced with "%20" for URL encoding.
		String searchUrl = "https://gutendex.com/books/?search=machado%20de%20assis";

		System.out.println("Buscando livros na API...");
		String jsonResponse = consumer.fetchData(searchUrl);

		System.out.println("\n--- RESPOSTA DA API ---");
		System.out.println(jsonResponse);
		System.out.println("-----------------------\n");
	}
}