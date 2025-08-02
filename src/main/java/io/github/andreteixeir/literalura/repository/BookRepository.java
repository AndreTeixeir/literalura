package io.github.andreteixeir.literalura.repository;

import io.github.andreteixeir.literalura.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repository interface for Book entities.
 * Uses Spring Data JPA to provide CRUD operations and custom queries.
 */
public interface BookRepository extends JpaRepository<Book, Long> {
    /**
     * Finds a book by its title, ignoring case and allowing partial matches.
     *
     * @param title The title (or part of the title) of the book to search for.
     * @return An Optional containing the found Book, or an empty Optional if no match is found.
     */
    Optional<Book> findByTitleContainingIgnoreCase(String title);
}