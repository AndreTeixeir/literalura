package io.github.andreteixeir.literalura.repository;

import io.github.andreteixeir.literalura.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByTitleContainingIgnoreCase(String title);
    List<Book> findByLanguage(String language);
    Optional<Book> findTopByOrderByDownloadCountDesc();
    Optional<Book> findTopByOrderByDownloadCountAsc();
    @Query("SELECT AVG(b.downloadCount) FROM Book b")
    Double getAverageDownloadCount();

    // *** NOVO MÉTODO PARA O TOP 10 ***
    List<Book> findTop10ByOrderByDownloadCountDesc();
}