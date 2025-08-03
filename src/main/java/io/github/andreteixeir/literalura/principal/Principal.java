package io.github.andreteixeir.literalura.principal;

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

import java.util.InputMismatchException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {
    private final Scanner scanner = new Scanner(System.in);
    private final ApiConsumer consumer = new ApiConsumer();
    private final IDataConverter converter = new DataConverter();
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final String API_BASE_URL = "https://gutendex.com/books/";

    public Principal(BookRepository bookRepository, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
    }

    public void displayMenu() {
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
            try {
                option = scanner.nextInt();
                scanner.nextLine();
            } catch (InputMismatchException e) {
                System.out.println("Erro: Por favor, digite um número inteiro válido.");
                scanner.nextLine();
                option = -1;
                continue;
            }
            switch (option) {
                case 1: searchBookByTitle(); break;
                case 2: listRegisteredBooks(); break;
                case 3: listRegisteredAuthors(); break;
                case 4: listAuthorsAliveInYear(); break;
                case 5: listBooksByLanguage(); break;
                case 0: System.out.println("Saindo do LiterAlura. Até a próxima!"); break;
                default: System.out.println("Opção inválida. Tente novamente.");
            }
        }
    }

    // *** MÉTODO REFEITO PARA PEGAR APENAS O PRIMEIRO RESULTADO ***
    private void searchBookByTitle() {
        System.out.println("Digite o título do livro que deseja buscar:");
        var bookTitle = scanner.nextLine();

        System.out.println("Digite o código do idioma para a busca (ex: en, pt, es, fr) ou deixe em branco para buscar em todos:");
        var langCode = scanner.nextLine();

        String searchUrl = API_BASE_URL + "?search=" + bookTitle.replace(" ", "%20");
        if (!langCode.isEmpty()) {
            searchUrl += "&languages=" + langCode;
        }

        System.out.println("Buscando na API...");
        String jsonResponse = consumer.fetchData(searchUrl);
        ApiResponseDTO apiResponse = converter.getData(jsonResponse, ApiResponseDTO.class);

        if (apiResponse != null && apiResponse.results() != null && !apiResponse.results().isEmpty()) {
            // Pega o primeiro livro da lista e tenta salvá-lo
            BookDTO selectedBookDTO = apiResponse.results().getFirst();
            saveBook(selectedBookDTO);
        } else {
            handleBookNotFound(bookTitle, langCode);
        }
    }

    private void handleBookNotFound(String bookTitle, String langCode) {
        if (langCode.isEmpty()) {
            System.out.println("Nenhum livro encontrado com o título '" + bookTitle + "'.");
            return;
        }

        System.out.println("Nenhum livro encontrado para o título '" + bookTitle + "' no idioma '" + langCode + "'.");
        System.out.println("Buscando em outros idiomas...");

        String generalSearchUrl = API_BASE_URL + "?search=" + bookTitle.replace(" ", "%20");
        String jsonResponse = consumer.fetchData(generalSearchUrl);
        ApiResponseDTO apiResponse = converter.getData(jsonResponse, ApiResponseDTO.class);

        if (apiResponse != null && apiResponse.results() != null && !apiResponse.results().isEmpty()) {
            List<String> availableLanguages = apiResponse.results().stream()
                    .flatMap(book -> book.languages().stream())
                    .distinct()
                    .collect(Collectors.toList());
            System.out.println("O livro foi encontrado nos seguintes idiomas: " + availableLanguages);
        } else {
            System.out.println("Este livro não foi encontrado em nenhum idioma.");
        }
    }

    // ... (Os outros métodos como saveBook, listRegisteredBooks, etc., continuam os mesmos) ...
    private void saveBook(BookDTO bookDTO) {
        Optional<Book> existingBook = bookRepository.findByTitleContainingIgnoreCase(bookDTO.title());
        if (existingBook.isPresent()) {
            System.out.println("Este livro já está cadastrado no banco de dados.");
        } else if (bookDTO.authors().isEmpty()) {
            System.out.println("Não foi possível salvar o livro pois não possui autor.");
        } else {
            Author author;
            AuthorDTO authorDTO = bookDTO.authors().getFirst();
            Optional<Author> existingAuthor = authorRepository.findByNameContainingIgnoreCase(authorDTO.name());

            if (existingAuthor.isPresent()) {
                author = existingAuthor.get();
            } else {
                author = new Author(authorDTO.name(), authorDTO.birthYear(), authorDTO.deathYear());
                authorRepository.save(author);
            }
            Book book = new Book(bookDTO.title(), bookDTO.languages().getFirst(), bookDTO.downloadCount(), author);
            bookRepository.save(book);
            System.out.println("\n--- Livro salvo com sucesso! ---\n" + book + "\n--------------------------------\n");
        }
    }

    private void listRegisteredBooks() {
        List<Book> books = bookRepository.findAll();
        if (books.isEmpty()) {
            System.out.println("\nNenhum livro cadastrado no banco de dados.\n");
        } else {
            System.out.println("\n--- Livros Registrados ---");
            books.forEach(b -> System.out.println(
                    "--------------------------\n" + " Título: " + b.getTitle() + "\n" + " Autor: " + b.getAuthor().getName() + "\n" + " Idioma: " + b.getLanguage() + "\n" + " Downloads: " + b.getDownloadCount() + "\n" + "--------------------------\n"
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
                        "---------------------------\n" + "Autor: " + a.getName() + "\n" + "Ano de Nascimento: " + a.getBirthYear() + "\n" + "Ano de Falecimento: " + a.getDeathYear() + "\n" + "Livros: " + bookTitles + "\n" + "---------------------------\n"
                );
            });
        }
    }

    private void listAuthorsAliveInYear() {
        System.out.println("\nDigite o ano para pesquisar os autores vivos:");
        var year = scanner.nextInt();
        scanner.nextLine();
        List<Author> authors = authorRepository.findAuthorsAliveInYear(year);
        if (authors.isEmpty()) {
            System.out.println("\nNenhum autor vivo encontrado para o ano de " + year + ".\n");
        } else {
            System.out.println("\n--- Autores Vivos em " + year + " ---");
            authors.forEach(a -> System.out.println(
                    "---------------------------\n" + "Autor: " + a.getName() + "\n" + "Ano de Nascimento: " + a.getBirthYear() + "\n" + "Ano de Falecimento: " + a.getDeathYear() + "\n" + "---------------------------\n"
            ));
        }
    }

    private void listBooksByLanguage() {
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
                    "--------------------------\n" + " Título: " + b.getTitle() + "\n" + " Autor: " + b.getAuthor().getName() + "\n" + "--------------------------\n"
            ));
        }
    }
}