package io.github.andreteixeir.literalura.model;

import jakarta.persistence.*;

@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String title;

    private String language;
    private Double downloadCount;

    @ManyToOne
    private Author author;

    // Default constructor
    public Book() {}

    // Constructor with parameters
    public Book(String title, String language, Double downloadCount, Author author) {
        this.title = title;
        this.language = language;
        this.downloadCount = downloadCount;
        this.author = author;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Double getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(Double downloadCount) {
        this.downloadCount = downloadCount;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    @Override
    public String toString() {
        return "Book{" +
                "title='" + title + '\'' +
                ", language='" + language + '\'' +
                ", downloadCount=" + downloadCount +
                ", author=" + (author != null ? author.getName() : "N/A") +
                '}';
    }
}