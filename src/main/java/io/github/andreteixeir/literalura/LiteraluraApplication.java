package io.github.andreteixeir.literalura;

import io.github.andreteixeir.literalura.dto.ApiResponseDTO;
import io.github.andreteixeir.literalura.dto.AuthorDTO;
import io.github.andreteixeir.literalura.dto.BookDTO;
import io.github.andreteixeir.literalura.model.Author;
import io.github.andreteixeir.literalura.model.Book;
import io.github.andreteixeir.literalura.repository.AuthorRepository;
import io.github.andreteixeir.literalura.repository.BookRepository;
import io.github.andreteixeir.literalura.service.ApiConsumer;
import io.github.andreteixeir.literalura.service.DataConverter;
import io.github.andreteixeir.literalura.service.IDataConverter;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

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
	public void run(String... args) {
		Scanner scanner = new Scanner(System.in);
		var option = -1;

		while (option != 0) {
			var menu = """
                    *** Bem-vindo ao LiterAlura ***
                    
                    Escolha uma das opções abaixo:
                    1 - Buscar livro pelo título
                    2 - Listar livros registrados
                    3 - Listar autores registrados
                    4 - Listar autores vivos em um determinado ano
                    5 - Listar livros em um determinado idioma
                    
                    0 - Sair
                    """;

			System.out.println(menu);
			option = scanner.nextInt();
			scanner.nextLine(); // Consume the newline character

			switch (option) {
				case 1:
					searchBookByTitle();
					break;
				case 2:
					listRegisteredBooks();
					break;
				case 3:
					listRegisteredAuthors();
					break;
				case 4:
					listAuthorsAliveInYear();
					break;
				case 5:
					// *** MUDANÇA AQUI ***
					listBooksByLanguage();
					break;
				case 0:
					System.out.println("Saindo do LiterAlura. Até a próxima!");
					break;
				default:
					System.out.println("Opção inválida. Tente novamente.");
			}
		}
	}

	private void searchBookByTitle() {
		ApiConsumer consumer = new ApiConsumer();
		IDataConverter converter = new DataConverter();
		Scanner scanner = new Scanner(System.in);
		System.out.println("Digite o título do livro que você deseja buscar:");
		var bookTitle = scanner.nextLine();
		var searchUrl = "https://gutendex.com/books/?search=" + bookTitle.replace(" ", "%20");
		System.out.println("Buscando...");
		String jsonResponse = consumer.fetchData(searchUrl);
		ApiResponseDTO apiResponse = converter.getData(jsonResponse, ApiResponseDTO.class);
		if (apiResponse != null && apiResponse.results() != null && !apiResponse.results().isEmpty()) {
			BookDTO foundBookDTO = apiResponse.results().getFirst();
			Optional<Book> existingBook = bookRepository.findByTitleContainingIgnoreCase(foundBookDTO.title());
			if (existingBook.isPresent()) {
				System.out.println("Este livro já está cadastrado no banco de dados.");
			} else {
				Author author;
				AuthorDTO authorDTO = foundBookDTO.authors().getFirst();
				Optional<Author> existingAuthor = authorRepository.findByNameContainingIgnoreCase(authorDTO.name());
				if (existingAuthor.isPresent()) {
					author = existingAuthor.get();
				} else {
					author = new Author(authorDTO.name(), authorDTO.birthYear(), authorDTO.deathYear());
					authorRepository.save(author);
				}
				Book book = new Book(foundBookDTO.title(), foundBookDTO.languages().getFirst(), foundBookDTO.downloadCount(), author);
				bookRepository.save(book);
				System.out.println("\n--- Livro salvo com sucesso! ---\n" + book + "\n--------------------------------\n");
			}
		} else {
			System.out.println("Nenhum livro encontrado com este título.");
		}
	}

	private void listRegisteredBooks() {
		List<Book> books = bookRepository.findAll();
		if (books.isEmpty()) {
			System.out.println("\nNenhum livro cadastrado no banco de dados.\n");
		} else {
			System.out.println("\n--- Livros Registrados ---");
			books.forEach(b -> System.out.println(
					"--------------------------\n" +
							" Título: " + b.getTitle() + "\n" +
							" Autor: " + b.getAuthor().getName() + "\n" +
							" Idioma: " + b.getLanguage() + "\n" +
							" Downloads: " + b.getDownloadCount() + "\n" +
							"--------------------------\n"
			));
		}
	}

	private void listRegisteredAuthors() {
		List<Author> authors = authorRepository.findAll();
		if (authors.isEmpty()) {
			System.out.println("\nNenhum autor cadastrado no banco de dados.\n");
		} else {
			System.out.println("\n--- Autores Registrados ---");
			authors.forEach(a -> {
				List<String> bookTitles = a.getBooks().stream().map(Book::getTitle).toList();
				System.out.println(
						"---------------------------\n" +
								"Autor: " + a.getName() + "\n" +
								"Ano de Nascimento: " + a.getBirthYear() + "\n" +
								"Ano de Falecimento: " + a.getDeathYear() + "\n" +
								"Livros: " + bookTitles + "\n" +
								"---------------------------\n"
				);
			});
		}
	}

	private void listAuthorsAliveInYear() {
		Scanner scanner = new Scanner(System.in);
		System.out.println("\nDigite o ano para pesquisar os autores vivos:");
		var year = scanner.nextInt();
		scanner.nextLine();
		List<Author> authors = authorRepository.findAuthorsAliveInYear(year);
		if (authors.isEmpty()) {
			System.out.println("\nNenhum autor vivo encontrado para o ano de " + year + ".\n");
		} else {
			System.out.println("\n--- Autores Vivos em " + year + " ---");
			authors.forEach(a -> System.out.println(
					"---------------------------\n" +
							"Autor: " + a.getName() + "\n" +
							"Ano de Nascimento: " + a.getBirthYear() + "\n" +
							"Ano de Falecimento: " + a.getDeathYear() + "\n" +
							"---------------------------\n"
			));
		}
	}

	// *** NOVO MÉTODO ADICIONADO AQUI ***
	private void listBooksByLanguage() {
		Scanner scanner = new Scanner(System.in);
		System.out.println("""
                Digite o idioma para a busca:
                es - espanhol
                en - inglês
                fr - francês
                pt - português
                """);
		var language = scanner.nextLine();
		List<Book> books = bookRepository.findByLanguage(language);

		if (books.isEmpty()) {
			System.out.println("\nNenhum livro encontrado para o idioma '" + language + "'.\n");
		} else {
			System.out.println("\n--- Livros em '" + language + "' ---");
			books.forEach(b -> System.out.println(
					"--------------------------\n" +
							" Título: " + b.getTitle() + "\n" +
							" Autor: " + b.getAuthor().getName() + "\n" +
							"--------------------------\n"
			));
		}
	}
}