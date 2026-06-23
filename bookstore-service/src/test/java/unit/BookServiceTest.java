package unit;

import org.example.database.BookRepository;
import org.example.mapper.BookMapper;
import org.example.model.*;
import org.example.model.Book;
import org.example.service.book.BookApiService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    @InjectMocks
    private BookApiService bookService;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @Test
    void getBooks_shouldReturnListBookDto() {
        Integer bookId = 1;

        BookEntity bookEntityOne = new BookEntity();
        BookEntity bookEntityTwo = new BookEntity();

        Book dtoOne = new Book();
        Book dtoTwo = new Book();

        List<BookEntity> entities = List.of(bookEntityOne, bookEntityTwo);

        Page<BookEntity> page = new PageImpl<>(entities);

        BookEntity bookEntity = new BookEntity();
        bookEntity.setId(bookId);
        bookEntity.setReviewCount(1);
        bookEntity.setAverageRating(3);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(bookEntity));
        when(bookMapper.toDto(bookEntityOne)).thenReturn(dtoOne);
        when(bookMapper.toDto(bookEntityTwo)).thenReturn(dtoTwo);
        when(bookRepository.findAll(
                any(Specification.class),
                any(PageRequest.class)
        )).thenReturn(page);

        List<Book> result = bookService.getBooks(bookId, null, null,
                null, null, null,
                null, null, null,
                null, 10, 0);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(dtoOne, result.get(0));
        assertEquals(dtoTwo, result.get(1));

        verify(bookRepository).findById(bookId);
        verify(bookMapper).toDto(bookEntityOne);
        verify(bookMapper).toDto(bookEntityTwo);
    }
}
