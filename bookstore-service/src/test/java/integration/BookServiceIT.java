package integration;

import jakarta.transaction.Transactional;
import org.example.Main;
import org.example.database.AuthorRepository;
import org.example.database.BookRepository;
import org.example.database.PublisherRepository;
import org.example.model.*;
import org.example.model.Genre;
import org.example.model.SortBook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        classes = Main.class,
        properties = {
                "JWT_SECRET=test-secret-key"
        }
)
@AutoConfigureMockMvc(addFilters = false)
@Transactional
public class BookServiceIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private PublisherRepository publisherRepository;

    private Integer bookId;
    private Integer authorId;
    private Integer publisherId;

    @BeforeEach
    void setUp() {
        AuthorEntity author = new AuthorEntity();
        author.setName("Aizek");
        author.setSurname("Azimov");
        author = authorRepository.save(author);

        PublisherEntity publisher = new PublisherEntity();
        publisher.setName("Line");
        publisher.setCountry("USA");
        publisher = publisherRepository.save(publisher);

        BookEntity book = new BookEntity();
        book.setTitle("Test Book");
        book.setAuthor(author);
        book.setPublisher(publisher);
        book.setGenre(Genre.COMEDY);
        book.setPrice(BigDecimal.valueOf(5000.0));
        book.setDescription("Description1");
        book.setPages(300);
        book.setReviewCount(5);
        book.setAverageRating(4);
        book.setReleaseDate(LocalDateTime.now());

        book = bookRepository.save(book);

        this.bookId = book.getId();
        this.authorId = author.getId();
        this.publisherId = publisher.getId();
    }

    @Test
    void getBooks_ShouldReturnBookList() throws Exception {
        mockMvc.perform(get("/books")
                        .param("id", String.valueOf(bookId))
                        .param("authorId", String.valueOf(authorId))
                        .param("publisherId", String.valueOf(publisherId))
                        .param("genre", Genre.COMEDY.name())
                        .param("sortBook", SortBook.ID.name())
                        .param("limit", "3")
                        .param("offset", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getBooks_ShouldReturnBadRequest_WhenInvalidGenre() throws Exception {
        mockMvc.perform(get("/books")
                        .param("genre", "NOT_EXISTING_GENRE"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBooks_ShouldNotFail_WhenOptionalFieldsAreNull() throws Exception {

        mockMvc.perform(get("/books")
                        .param("limit", "3")
                        .param("offset", "0"))
                .andExpect(status().isOk());
    }
}