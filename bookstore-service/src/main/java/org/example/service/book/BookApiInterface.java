package org.example.service.book;

import org.example.model.Book;
import org.example.model.Genre;
import org.example.model.Order;
import org.example.model.SortBook;

import java.util.List;

public interface BookApiInterface {

    List<Book> getBooks(Integer id,
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
                        Integer offset);
}