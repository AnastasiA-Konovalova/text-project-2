package integration;

import jakarta.transaction.Transactional;
import org.example.Main;
import org.example.database.*;
import org.example.model.AuthorEntity;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Main.class, properties = {
        "JWT_SECRET=test-secret-key"
})
@AutoConfigureMockMvc(addFilters = false)
@Transactional
public class AuthorServiceIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private PublisherRepository publisherRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    private AuthorEntity author1;

    @BeforeEach
    void setUp() {
        reviewRepository.deleteAll();
        bookRepository.deleteAll();
        authorRepository.deleteAll();
        publisherRepository.deleteAll();
        userRepository.deleteAll();

        author1 = new AuthorEntity();
        author1.setName("Stiven");
        author1.setSurname("King");
        author1.setPseudonym("Stiven");
        author1.setMiddleName(null);

        AuthorEntity author2 = new AuthorEntity();
        author2.setName("Aizek");
        author2.setSurname("Azimov");
        author2.setPseudonym("Aizek");
        author2.setMiddleName(null);

        AuthorEntity author3 = new AuthorEntity();
        author3.setName("Klim");
        author3.setSurname("Ivanov");
        author3.setPseudonym("Klim");
        author3.setMiddleName(null);

        authorRepository.saveAll(List.of(author1, author2, author3));
    }

    @Test
    void getPopularAuthors_ShouldReturnLimitedAndOffsetList() throws Exception {
        int limit = 3;
        int offset = 0;

        mockMvc.perform(get("/authors/popular")
                .param("limit", String.valueOf(limit))
                .param("offset", String.valueOf(offset)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    void getPopularAuthors_ShouldThrowException_WhenLimitIsNegative() throws Exception {
        int limit = -1;
        int offset = 0;

        mockMvc.perform(get("/authors/popular")
                .param("limit", String.valueOf(limit))
                .param("offset", String.valueOf(offset)))
                        .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void getAuthorById_ShouldReturnAuthor() throws Exception {
        mockMvc.perform(get("/author/{authorId}", author1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(author1.getId()))
                .andExpect(jsonPath("$.name").value("Stiven"))
                .andExpect(jsonPath("$.surname").value("King"));
    }

    @Test
    void getAuthorById_ShouldThrowException_WhenIdIsNotExists() throws Exception {
        int authorId = 5000;

        mockMvc.perform(get("/author/{authorId}", authorId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Author not found"));
    }
}