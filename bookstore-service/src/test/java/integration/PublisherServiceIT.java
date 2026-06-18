package integration;

import jakarta.transaction.Transactional;
import org.example.Main;
import org.example.database.PublisherRepository;
import org.example.model.PublisherEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Main.class, properties = {
        "JWT_SECRET=test-secret-key"
})
@AutoConfigureMockMvc(addFilters = false)
@Transactional
public class PublisherServiceIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PublisherRepository publisherRepository;

    @BeforeEach
    void setUp() {
        PublisherEntity publisherEntity = new PublisherEntity();
        publisherEntity.setId(1);
        publisherEntity.setName("Line");
        publisherEntity.setDescription("Description1");
        publisherEntity.setCreatedAt(LocalDateTime.now());
        publisherEntity.setUpdatedAt(LocalDateTime.now());
        publisherEntity.setCountry("USA");

        publisherRepository.saveAll(List.of(publisherEntity));
    }

    @Test
    void getPublisherById_ShouldReturnPublisher() throws Exception {
        int publisherId = 1;

        mockMvc.perform(get("/publisher/{publisherId}", publisherId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(publisherId))
                .andExpect(jsonPath("$.name").value("Line"))
                .andExpect(jsonPath("$.description").value("Description1"))
                .andExpect(jsonPath("$.country").value("USA"));

    }

    @Test
    void getPublisherById_ShouldThrowException_WhenIdIsNotExists() throws Exception {
        int authorId = 5000;

        mockMvc.perform(get("/publisher/{publisherId}", authorId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Publisher not found"));
    }
}