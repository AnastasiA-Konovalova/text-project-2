package org.example.service.book;

import lombok.RequiredArgsConstructor;
import org.example.database.BookApiRepository;
import org.example.exeception.ClassNotFoundException;
import org.example.mapper.BookMapper;
import org.example.model.Book;
import org.example.model.BookEntity;
import org.example.model.Genre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookApiService implements BookApiInterface {

    private final BookApiRepository bookRepository;
    private final BookMapper bookMapper;

    @Override
    public List<Book> getBooks(Integer id,
                               Integer authorId,
                               Integer seriesId,
                               Integer publisherId,
                               Genre genre,
                               String publisherName,
                               Boolean isPopular,
                               Boolean isNew,
                               Integer limit,
                               Integer offset,
                               String sortBook,
                               String order) {
        if (id != null) {
            existBookEntity(id);
        }
        Sort sort;
        if ("title".equalsIgnoreCase(sortBook)) sort = Sort.by("title");
        else if ("price".equalsIgnoreCase(sortBook)) sort = Sort.by("price");
        else sort = Sort.by("id");

        if ("desc".equalsIgnoreCase(order)) sort = sort.descending();

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
        List<Book> books = page.getContent().stream()
                .map(bookMapper::toDto)
                .toList();
        return books;
    }

    private BookEntity existBookEntity(Integer id) {
        return bookRepository.findById(id).orElseThrow(() -> new ClassNotFoundException("Book not found"));
    }
}