package org.example.service.book;

import lombok.RequiredArgsConstructor;
import org.example.database.BookRepository;
import org.example.exception.NotFoundException;
import org.example.mapper.BookMapper;
import org.example.mapper.SortBookMapper;
import org.example.model.*;
import org.example.model.Book;
import org.example.model.Genre;
import org.example.model.Order;
import org.example.model.SortBook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookApiService implements BookApiInterface {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Override
    public List<Book> getBooks(Integer id,
                               Integer authorId,
                               Integer seriesId,
                               Integer publisherId,
                               Genre genre,
                               SortBook sortBook,
                               String publisherName,
                               Boolean isPopular,
                               Boolean isNew,
                               Order order,
                               Integer limit,
                               Integer offset) {
        if (id != null) {
            existBookEntity(id);
        }
        Sort sort = Sort.by("id");

        if (sortBook != null) {
            sort = Sort.by(SortBookMapper.toField(sortBook));
        }

        if (order == Order.DESC) sort = sort.descending();

        PageRequest pageRequest = PageRequest.of(
                offset / limit,
                limit,
                sort
        );

        Specification<BookEntity> specification = Specification.where(BookSpecification.hasId(id))
                .and(BookSpecification.hasAuthor(authorId))
                .and(BookSpecification.hasGenre(genre))
                .and(BookSpecification.hasPublisher(publisherId))
                .and(BookSpecification.hasSeries(seriesId))
                .and(BookSpecification.hasNew(isNew))
                .and(BookSpecification.hasPopular(isPopular))
                .and(BookSpecification.hasPublisherName(publisherName));

        Page<BookEntity> page = bookRepository.findAll(specification, pageRequest);
        return page.getContent().stream()
                .map(bookMapper::toDto)
                .toList();
    }

    private BookEntity existBookEntity(Integer id) {
        return bookRepository.findById(id).orElseThrow(() -> new NotFoundException("Book not found"));
    }
}