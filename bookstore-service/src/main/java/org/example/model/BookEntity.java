package org.example.model;

import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.Getter;
import lombok.Setter;
import org.example.model.Genre;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "books")
public class BookEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "title", nullable = false, length = 2000)
    private String title;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private AuthorEntity author;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(columnDefinition = "book_genre")
    private Genre genre;

    @ManyToOne
    @JoinColumn(name = "publisher_id", nullable = false)
    private PublisherEntity publisher;

    @ManyToOne
    @JoinColumn(name = "series_id")
    private PublisherSeriesEntity series;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "isbn")
    private String isbn;

    @Column(name = "release")
    private String release;

    @Column(name = "release_date")
    private LocalDateTime releaseDate;

    @Column(name = "description", nullable = false, length = 50000)
    private String description;

    @Column(name = "pages", nullable = false)
    private Integer pages;

    @Column(name = "weight")
    private Double weight;

    @Column(name = "review_count", nullable = false)
    private Integer reviewCount;

    @Column(name = "average_rating", nullable = false)
    private Integer averageRating;
}
