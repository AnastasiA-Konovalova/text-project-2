package org.example.controller.books;

import lombok.RequiredArgsConstructor;
import org.example.model.Book;
import org.example.api.BookApi;
import org.example.model.Genre;
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
                                               String publisherName,
                                               Boolean isPopular,
                                               Boolean isNew,
                                               Integer limit,
                                               Integer offset,
                                               String sortBook,
                                               String order) {
        return ResponseEntity.ok(bookInterface.getBooks(id, authorId, seriesId, publisherId, genre, publisherName, isPopular, isNew, limit, offset, sortBook, order));
    }
}