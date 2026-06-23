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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Main.class, properties = {
        "JWT_SECRET=test-secret-key"
})
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser(username = "email3@mail.ru")
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
        reviewRepository.deleteAll();
        bookRepository.deleteAll();
        authorRepository.deleteAll();
        publisherRepository.deleteAll();
        userRepository.deleteAll();

        UserEntity user = new UserEntity();
        user.setName("Ivan");
        user.setSurname("Ivanov");
        user.setEmail("email3@mail.ru");
        user.setMiddleName(null);
        user.setPassword("password1");
        user.setPhoneNumber("89123568854");
        userRepository.flush();

        UserEntity savedUser = userRepository.save(user);

        AuthorEntity author = new AuthorEntity();
        author.setName("Aizek");
        author.setSurname("Azimov");
        author = authorRepository.save(author);
        authorRepository.flush();

        book = new BookEntity();
        book.setTitle("Test Book");
        book.setAuthor(author);
        book.setGenre(Genre.COMEDY);
        book.setPrice(BigDecimal.valueOf(5000.0));
        book.setDescription("Description1");
        book.setPages(300);
        book.setReviewCount(5);
        book.setAverageRating(4);
        book.setReleaseDate(LocalDateTime.now());
        bookRepository.flush();

        PublisherEntity publisher = new PublisherEntity();
        publisher.setName("Line");
        publisher.setCountry("USA");
        publisher = publisherRepository.save(publisher);
        publisherRepository.flush();
        book.setPublisher(publisher);

        book = bookRepository.save(book);

        reviewEntity = new ReviewEntity();
        reviewEntity.setReviewer(savedUser);
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
        mockMvc.perform(patch("/review/{reviewId}", reviewEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(jsonPath("$.rating").value(4))
                .andExpect(jsonPath("$.text").value("New text"))
                .andExpect(status().isOk());
    }

    @Test
    void postChangeReview_ShouldThrowNotFound() throws Exception {
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
        reviewEntity = reviewRepository.save(reviewEntity);

        mockMvc.perform(get("/review/{reviewId}", reviewEntity.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(reviewEntity.getId()))
                .andExpect(jsonPath("$.text").value("Old text"))
                .andExpect(jsonPath("$.rating").value(5));
    }

    @Test
    void getBookReviewById_ShouldReturnNotFound_WhenReviewNotFound() throws Exception {
        int reviewId = 9999;

        mockMvc.perform(get("/review/{reviewId}", reviewId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getReviews_ReturnListBookReview() throws Exception {
        int limit = 3;
        int offset = 0;

        mockMvc.perform(get("/review")
                        .param("bookId", String.valueOf(book.getId()))
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
        mockMvc.perform(delete("/review/{reviewId}", reviewEntity.getId()))
                .andExpect(status().isCreated());
                        assertFalse(reviewRepository.existsById(reviewEntity.getId()));
    }

    @Test
    void deleteReviewById_WhenIdIsNotExists() throws Exception {
        mockMvc.perform(delete("/review/{reviewId}", 9999))
                .andExpect(status().isNotFound());
    }
}
