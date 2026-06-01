package org.example.mapper;

import lombok.RequiredArgsConstructor;
import org.example.model.Author;
import org.example.model.Book;
import org.example.model.BookEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.ZoneOffset;

@Component
@RequiredArgsConstructor
public class BookMapper {

    private final PublisherSeriesMapper seriesMapper;

    public Book toDto(BookEntity bookEntity) {
        Book dto = new Book();
        dto.setId(bookEntity.getId());
        dto.setTitle(bookEntity.getTitle());

        Author authorDto = new Author();
        authorDto.setId(bookEntity.getAuthor().getId());
        authorDto.setName(bookEntity.getAuthor().getName());
        authorDto.setSurname(bookEntity.getAuthor().getSurname());
        authorDto.setMiddleName(bookEntity.getAuthor().getMiddleName());
        authorDto.setPseudonym(bookEntity.getAuthor().getPseudonym());

        dto.setAuthor(authorDto);

        dto.setGenre(bookEntity.getGenre());
        dto.setPublisherId(bookEntity.getPublisher().getId());
        dto.setSeries(seriesMapper.toDto(bookEntity.getSeries()));
        dto.setAverageRating(BigDecimal.valueOf(bookEntity.getAverageRating()));
        dto.setDescription(bookEntity.getDescription());
        dto.setISBN(bookEntity.getIsbn());
        dto.setPages(bookEntity.getPages());
        dto.setPrice(bookEntity.getPrice());
        dto.setReleaseDate(bookEntity.getReleaseDate().atOffset(ZoneOffset.UTC));
        dto.setReviewCount(bookEntity.getReviewCount());
        dto.setWeight(bookEntity.getWeight());

        return dto;
    }
}