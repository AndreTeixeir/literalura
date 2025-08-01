package io.github.andreteixeir.literalura.repository;

import io.github.andreteixeir.literalura.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}