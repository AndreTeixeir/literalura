package io.github.andreteixeir.literalura.repository;

import io.github.andreteixeir.literalura.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<Author, Long> {
}