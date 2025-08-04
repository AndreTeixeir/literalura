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
                    6 - Gerar estatísticas do banco de dados
                    7 - Listar Top 10 livros mais baixados
                    8 - Buscar autor por nome
                    
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
                case 6: showDatabaseStatistics(); break;
                case 7: listTop10Books(); break;
                case 8: findAuthorByName(); break;
                case 0: System.out.println("Saindo do LiterAlura. Até a próxima!"); break;
                default: System.out.println("Opção inválida. Tente novamente.");
            }
        }
    }

    private void searchBookByTitle() {
        System.out.println("Digite o título do livro que deseja buscar:");
        var bookTitle = scanner.nextLine();
        var searchUrl = API_BASE_URL + "?search=" + bookTitle.replace(" ", "%20");
        System.out.println("Buscando na API...");
        String jsonResponse = consumer.fetchData(searchUrl);
        ApiResponseDTO apiResponse = converter.getData(jsonResponse, ApiResponseDTO.class);

        if (apiResponse != null && !apiResponse.results().isEmpty()) {
            BookDTO foundBookDTO = apiResponse.results().getFirst();
            saveBook(foundBookDTO);
        } else {
            System.out.println("Nenhum livro encontrado com o título '" + bookTitle + "'.");
        }
    }

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

    private void showDatabaseStatistics() {
        long bookCount = bookRepository.count();
        long authorCount = authorRepository.count();
        Double averageDownloads = bookRepository.getAverageDownloadCount();
        Optional<Book> mostDownloadedBook = bookRepository.findTopByOrderByDownloadCountDesc();
        Optional<Book> leastDownloadedBook = bookRepository.findTopByOrderByDownloadCountAsc();
        System.out.println("\n--- Estatísticas do Banco de Dados ---");
        System.out.println("--------------------------------------");
        System.out.println("Total de livros registrados: " + bookCount);
        System.out.println("Total de autores registrados: " + authorCount);
        System.out.printf("Média de downloads por livro: %.2f\n", averageDownloads != null ? averageDownloads : 0.0);
        mostDownloadedBook.ifPresent(book -> System.out.println("Livro com mais downloads: '" + book.getTitle() + "' (" + book.getDownloadCount() + " downloads)"));
        leastDownloadedBook.ifPresent(book -> System.out.println("Livro com menos downloads: '" + book.getTitle() + "' (" + book.getDownloadCount() + " downloads)"));
        System.out.println("--------------------------------------\n");
    }

    private void listTop10Books() {
        List<Book> topBooks = bookRepository.findTop10ByOrderByDownloadCountDesc();
        if (topBooks.isEmpty()) {
            System.out.println("\nNão há livros suficientes no banco para gerar um Top 10.\n");
        } else {
            System.out.println("\n--- Top 10 Livros Mais Baixados ---");
            topBooks.forEach(b -> System.out.println(
                    "--------------------------\n" + " Título: " + b.getTitle() + "\n" + " Autor: " + b.getAuthor().getName() + "\n" + " Downloads: " + b.getDownloadCount() + "\n" + "--------------------------\n"
            ));
        }
    }

    private void findAuthorByName() {
        System.out.println("\nDigite o nome do autor que deseja buscar:");
        var authorName = scanner.nextLine();
        Optional<Author> author = authorRepository.findByNameContainingIgnoreCase(authorName);
        if (author.isPresent()) {
            Author foundAuthor = author.get();
            System.out.println("\n--- Autor Encontrado ---");
            System.out.println("---------------------------");
            System.out.println("Autor: " + foundAuthor.getName());
            System.out.println("Ano de Nascimento: " + foundAuthor.getBirthYear());
            System.out.println("Ano de Falecimento: " + foundAuthor.getDeathYear());
            List<String> bookTitles = foundAuthor.getBooks().stream().map(Book::getTitle).toList();
            System.out.println("Livros: " + bookTitles);
            System.out.println("---------------------------\n");
        } else {
            System.out.println("\nNenhum autor encontrado com o nome '" + authorName + "'.\n");
        }
    }
}