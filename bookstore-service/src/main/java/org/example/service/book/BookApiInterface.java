package org.example.service.book;

import org.example.model.Book;
import org.example.model.BookEntity;
import org.example.model.Genre;

import java.util.List;

public interface BookApiInterface {

    List<Book> getBooks(Integer id,
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
                        String order);
}
