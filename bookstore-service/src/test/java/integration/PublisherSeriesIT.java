package integration;

import jakarta.transaction.Transactional;
import org.example.Main;
import org.example.database.PublisherSeriesRepository;
import org.example.model.PublisherEntity;
import org.example.model.PublisherSeriesEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Main.class, properties = {
        "JWT_SECRET=test-secret-key"
})
@AutoConfigureMockMvc(addFilters = false)
@Transactional
public class PublisherSeriesIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PublisherSeriesRepository publisherSeriesRepository;

    @BeforeEach
    void setUp() {
        PublisherSeriesEntity publisherSeriesEntity = new PublisherSeriesEntity();
        publisherSeriesEntity.setId(1);
        publisherSeriesEntity.setName("Friend");
        publisherSeriesEntity.setCreatedAt(LocalDateTime.now());
        publisherSeriesEntity.setUpdatedAt(LocalDateTime.now());
        PublisherEntity publisherEntity = new PublisherEntity();
        publisherEntity.setId(1);

        publisherSeriesEntity.setPublisherId(publisherEntity.getId());

        publisherSeriesRepository.save(publisherSeriesEntity);
    }

    @Test
    void getPublisherSeriesById_ShouldReturnPublisherSeries() throws Exception {
        int publisherSeries = 1;

        mockMvc.perform(get("/publisherSeries/{publisherSeriesId}", publisherSeries))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(publisherSeries))
                .andExpect(jsonPath("$.name").value("Friend"));
    }

    @Test
    void ggetPublisherSeriesById_ShouldThrowException_WhenIdIsNotExists() throws Exception {
        int publisherSeries = 5000;

        mockMvc.perform(get("/publisherSeries/{publisherSeriesId}", publisherSeries))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("PublisherSeries not found"));
    }
}