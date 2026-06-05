package org.example.controller.books;

import lombok.RequiredArgsConstructor;
import org.example.model.Book;
import org.example.api.BookApi;
import org.example.model.Genre;
import org.example.model.Order;
import org.example.model.SortBook;
import org.example.service.book.BookApiInterface;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BooksApiImpl implements BookApi {

    private final BookApiInterface bookInterface;

    @Override
    public ResponseEntity<List<Book>> getBooks(Integer id,
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
        return ResponseEntity.ok(bookInterface.getBooks(id, authorId, seriesId, publisherId, genre, sortBook, publisherName, isPopular, isNew, order, limit, offset));
    }
}