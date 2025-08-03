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

public class Principal {
    private final Scanner scanner = new Scanner(System.in);
    private final ApiConsumer consumer = new ApiConsumer();
    private final IDataConverter converter = new DataConverter();
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

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

            // *** MUDANÇA AQUI PARA TRATAR ERRO ***
            try {
                option = scanner.nextInt();
                scanner.nextLine();
            } catch (InputMismatchException e) {
                System.out.println("Erro: Por favor, digite um número inteiro válido.");
                scanner.nextLine(); // Limpa o buffer do scanner
                option = -1; // Reseta a opção para continuar no loop
                continue; // Pula para a próxima iteração do loop
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

    // ... (Todos os outros métodos permanecem exatamente iguais) ...
    private void searchBookByTitle() {
        System.out.println("Digite o nome do livro ou palavra-chave para a busca:");
        var bookTitle = scanner.nextLine();
        var searchUrl = "https://gutendex.com/books/?search=" + bookTitle.replace(" ", "%20");
        System.out.println("Buscando na API...");
        String jsonResponse = consumer.fetchData(searchUrl);
        ApiResponseDTO apiResponse = converter.getData(jsonResponse, ApiResponseDTO.class);
        if (apiResponse == null || apiResponse.results() == null || apiResponse.results().isEmpty()) {
            System.out.println("Nenhum livro encontrado com base na sua busca.");
            return;
        }
        List<BookDTO> titleMatches = apiResponse.results().stream()
                .filter(b -> b.title().toLowerCase().contains(bookTitle.toLowerCase()))
                .toList();
        if (titleMatches.isEmpty()) {
            System.out.println("Nenhum livro encontrado com o título '" + bookTitle + "'. Tente uma busca mais ampla.");
            return;
        }
        System.out.println("\n--- Livros encontrados com o título '" + bookTitle + "' ---");
        for (int i = 0; i < titleMatches.size(); i++) {
            BookDTO book = titleMatches.get(i);
            System.out.printf("%d - Título: %s (Autor: %s)\n",
                    i + 1,
                    book.title(),
                    book.authors().isEmpty() ? "Desconhecido" : book.authors().getFirst().name());
        }
        System.out.println("----------------------------------------------");
        System.out.println("Digite o número do livro que deseja salvar ou 0 para cancelar:");
        int choice = scanner.nextInt();
        scanner.nextLine();
        if (choice > 0 && choice <= titleMatches.size()) {
            BookDTO selectedBookDTO = titleMatches.get(choice - 1);
            saveBook(selectedBookDTO);
        } else {
            System.out.println("Operação cancelada.");
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
}