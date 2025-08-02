package io.github.andreteixeir.literalura.repository;

import io.github.andreteixeir.literalura.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Author entities.
 * Uses Spring Data JPA to provide CRUD operations and custom queries.
 */
public interface AuthorRepository extends JpaRepository<Author, Long> {
    /**
     * Finds an author by their name, ignoring case and allowing partial matches.
     *
     * @param name The name of the author to search for.
     * @return An Optional containing the found Author, or an empty Optional if no match is found.
     */
    Optional<Author> findByNameContainingIgnoreCase(String name);

    /**
     * Custom query to find authors who were alive in a given year.
     *
     * @param year The year to check.
     * @return A list of authors who were alive during the specified year.
     */
    @Query("SELECT a FROM Author a WHERE a.birthYear <= :year AND a.deathYear >= :year")
    List<Author> findAuthorsAliveInYear(Integer year);
}