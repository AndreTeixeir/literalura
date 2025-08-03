package io.github.andreteixeir.literalura;

import io.github.andreteixeir.literalura.principal.Principal;
import io.github.andreteixeir.literalura.repository.AuthorRepository;
import io.github.andreteixeir.literalura.repository.BookRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LiteraluraApplication implements CommandLineRunner {

	private final BookRepository bookRepository;
	private final AuthorRepository authorRepository;

	public LiteraluraApplication(BookRepository bookRepository, AuthorRepository authorRepository) {
		this.bookRepository = bookRepository;
		this.authorRepository = authorRepository;
	}

	public static void main(String[] args) {
		SpringApplication.run(LiteraluraApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Principal principal = new Principal(bookRepository, authorRepository);
		principal.displayMenu();
	}
}