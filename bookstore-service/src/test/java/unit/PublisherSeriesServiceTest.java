package unit;

import org.example.database.PublisherSeriesRepository;
import org.example.mapper.PublisherSeriesMapper;
import org.example.model.PublisherSeries;
import org.example.model.PublisherSeriesEntity;
import org.example.service.publisherSeries.PublisherSeriesApiService;
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
public class PublisherSeriesServiceTest {

    @InjectMocks
    private PublisherSeriesApiService publisherSeriesService;

    @Mock
    private PublisherSeriesRepository publisherSeriesRepository;

    @Mock
    private PublisherSeriesMapper publisherSeriesMapper;

    @Test
    void getPublisherSeriesById_shouldReturnPublisherSeriesDto() {
        Integer publisherSeriesId = 1;

        PublisherSeriesEntity entity = new PublisherSeriesEntity();
        entity.setId(publisherSeriesId);

        PublisherSeries dto = new PublisherSeries();
        dto.setId(publisherSeriesId);

        when(publisherSeriesRepository.findById(publisherSeriesId))
                .thenReturn(Optional.of(entity));
        when(publisherSeriesMapper.toDto(entity))
                .thenReturn(dto);

        PublisherSeries result = publisherSeriesService.getPublisherSeriesById(publisherSeriesId);

        assertNotNull(result);
        assertEquals(publisherSeriesId, result.getId());

        verify(publisherSeriesRepository).findById(publisherSeriesId);
        verify(publisherSeriesMapper).toDto(entity);
    }
}
