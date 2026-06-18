package integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.example.Main;
import org.example.database.*;
import org.example.model.*;
import org.example.model.BookReviewInput;
import org.example.model.ChangeReviewRequest;
import org.example.model.Genre;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Main.class, properties = {
        "JWT_SECRET=test-secret-key"
})
@AutoConfigureMockMvc(addFilters = false)
@Transactional
public class ReviewServiceIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private PublisherRepository publisherRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private BookEntity book;

    private ReviewEntity reviewEntity;

    private ChangeReviewRequest request;

    @BeforeEach
    void setUp() {
        UserEntity user = new UserEntity();
        user.setName("Ivan");
        user.setSurname("Ivanov");
        user.setEmail("email1@mail.ru" + System.nanoTime());
        user.setMiddleName(null);
        user.setPassword("password1");
        user.setPhoneNumber("89123568854");

        book = new BookEntity();
        book.setTitle("Test Book");

        AuthorEntity author = new AuthorEntity();
        author.setName("Aizek");
        author.setSurname("Azimov");
        author = authorRepository.save(author);

        book.setAuthor(author);
        book.setGenre(Genre.COMEDY);
        book.setPrice(BigDecimal.valueOf(5000.0));
        book.setDescription("Description1");
        book.setPages(300);
        book.setReviewCount(5);
        book.setAverageRating(4);
        book.setReleaseDate(LocalDateTime.now());

        PublisherEntity publisher = new PublisherEntity();
        publisher.setName("Line");
        publisher.setCountry("USA");
        publisher = publisherRepository.save(publisher);
        book.setPublisher(publisher);

        book = bookRepository.save(book);

        UserEntity savedUser = userRepository.save(user);
        userRepository.save(savedUser);

        reviewEntity = new ReviewEntity();
        reviewEntity.setReviewer(userRepository.findByEmail("email1@mail.ru").orElseThrow());
        reviewEntity.setBook(book);
        reviewEntity.setText("Old text");
        reviewEntity.setRating(5);
        reviewEntity = reviewRepository.save(reviewEntity);

        request = new ChangeReviewRequest();
        request.setRating(4);
        request.setText("New text");
    }

    @Test
    void postReview_ShouldReturnBookReview() throws Exception {
        int ratingId = 5;

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "email1@mail.ru",
                        null,
                        List.of()
                )
        );
        BookReviewInput bookReviewInput = new BookReviewInput();
        bookReviewInput.setBookId(book.getId());
        bookReviewInput.setRating(ratingId);
        bookReviewInput.setText("Text review");

        mockMvc.perform(post("/review")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookReviewInput)))
                .andExpect(jsonPath("$.rating").value(5))
                .andExpect(jsonPath("$.text").value("Text review"))
                .andExpect(status().isOk());
    }

    @Test
    void postReview_invalidBookId_shouldReturnNotFound() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "test@mail.com",
                        null,
                        List.of()
                )
        );

        BookReviewInput input = new BookReviewInput();
        input.setBookId(999999);
        input.setRating(5);
        input.setText("Text");

        mockMvc.perform(post("/review")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isNotFound());
    }

    @Test
    void postReview_invalidRating_shouldReturnBadRequest() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "test@mail.com",
                        null,
                        List.of()
                )
        );

        BookReviewInput input = new BookReviewInput();
        input.setBookId(book.getId());
        input.setRating(999);
        input.setText("Text");

        mockMvc.perform(post("/review")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void patchChangeReview_ShouldReturnBookReview() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "email1@mail.ru",
                        null,
                        List.of()
                )
        );



        mockMvc.perform(patch("/review/{reviewId}", reviewEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(jsonPath("$.rating").value(4))
                .andExpect(jsonPath("$.text").value("New text"))
                .andExpect(status().isOk());
    }

    @Test
    void postChangeReview_ShouldThrowNotFound() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "email1@mail.ru",
                        null,
                        List.of()
                )
        );

        ChangeReviewRequest request = new ChangeReviewRequest();
        request.setRating(4);
        request.setText("New text");

        mockMvc.perform(patch("/review/{reviewId}", 9999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void getBookReviewById_ReturnBookReview() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "email1@mail.ru",
                        null,
                        List.of()
                )
        );

        reviewEntity = reviewRepository.save(reviewEntity);

        mockMvc.perform(get("/review/{reviewId}", reviewEntity.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(reviewEntity.getId()))
                .andExpect(jsonPath("$.text").value("Old text"))
                .andExpect(jsonPath("$.rating").value(5));
    }

    @Test
    void getBookReviewById_ShouldReturnNotFound_WhenReviewNotFound() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "email1@mail.ru",
                        null,
                        List.of()
                )
        );
        int reviewId = 9999;

        mockMvc.perform(get("/review/{reviewId}", reviewId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getReviews_ReturnListBookReview() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "email1@mail.ru",
                        null,
                        List.of()
                )
        );
        int bookId = 1;
        int limit = 3;
        int offset = 0;

        mockMvc.perform(get("/review")
                        .param("bookId", String.valueOf(bookId))
                        .param("limit", String.valueOf(limit))
                        .param("offset", String.valueOf(offset)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getReviews_ShouldThrowException_WhenLimitIsNegative() throws Exception {
        int limit = -1;
        int offset = 0;

        mockMvc.perform(get("/review")
                        .param("limit", String.valueOf(limit))
                        .param("offset", String.valueOf(offset)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void deleteReviewById_Success() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "email1@mail.ru",
                        null,
                        List.of()
                )
        );

        mockMvc.perform(delete("/review/{reviewId}", reviewEntity.getId()))
                .andExpect(status().isCreated());
                        assertFalse(reviewRepository.existsById(reviewEntity.getId()));
    }

    @Test
    void deleteReviewById_WhenIdIsNotExists() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "email1@mail.ru",
                        null,
                        List.of()
                )
        );

        mockMvc.perform(delete("/review/{reviewId}", 9999))
                .andExpect(status().isNotFound());
    }
}
