package io.github.andreteixeir.literalura.repository;

import io.github.andreteixeir.literalura.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; // Verifique se esta importação existe
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByTitleContainingIgnoreCase(String title);

    // Adicione este novo método:
    List<Book> findByLanguage(String language);
}