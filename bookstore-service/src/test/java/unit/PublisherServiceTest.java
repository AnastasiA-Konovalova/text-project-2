package unit;

import org.example.database.PublisherRepository;
import org.example.mapper.PublisherMapper;
import org.example.model.Publisher;
import org.example.model.PublisherEntity;
import org.example.service.publisher.PublisherApiService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PublisherServiceTest {

    @InjectMocks
    private PublisherApiService publisherService;

    @Mock
    private PublisherRepository publisherRepository;

    @Mock
    private PublisherMapper publisherMapper;

    @Test
    void getPublisherById_shouldReturnPublisherDto() {
        Integer publisherId = 1;

        PublisherEntity entity = new PublisherEntity();
        entity.setId(publisherId);

        Publisher dto = new Publisher();
        dto.setId(publisherId);

        when(publisherRepository.findById(publisherId))
                .thenReturn(Optional.of(entity));
        when(publisherMapper.toDto(entity))
                .thenReturn(dto);

        Publisher result = publisherService.getPublisherById(publisherId);

        assertNotNull(result);
        assertEquals(publisherId, result.getId());

        verify(publisherRepository).findById(publisherId);
        verify(publisherMapper).toDto(entity);
    }
}
