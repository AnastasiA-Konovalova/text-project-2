package org.example.service.publisherSeries;

import lombok.RequiredArgsConstructor;
import org.example.database.PublisherSeriesApiRepository;
import org.example.exeception.NotFoundException;
import org.example.mapper.PublisherSeriesMapper;
import org.example.model.PublisherSeries;
import org.example.model.PublisherSeriesEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PublisherSeriesApiService implements PublisherSeriesApiInterface {

    private final PublisherSeriesApiRepository publisherSeriesRepository;
    private final PublisherSeriesMapper publisherSeriesMapper;

    @Override
    public PublisherSeries getPublisherSeriesById(Integer publisherSeriesId) {
        PublisherSeriesEntity publisherSeries = existBookEntity(publisherSeriesId);
        return publisherSeriesMapper.toDto(publisherSeries);
    }

    private PublisherSeriesEntity existBookEntity(Integer publisherSeriesId) {
        return publisherSeriesRepository.findById(publisherSeriesId).orElseThrow(() -> new NotFoundException("PublisherSeries not found"));
    }
}