package org.example.controller.publisherSeries;

import lombok.RequiredArgsConstructor;
import org.example.api.PublisherSeriesApi;
import org.example.model.PublisherSeries;
import org.example.service.publisherSeries.PublisherSeriesApiInterface;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PublisherSeriesApiImpl implements PublisherSeriesApi {

    private final PublisherSeriesApiInterface publisherSeriesInterface;

    @Override
    public ResponseEntity<PublisherSeries> getPublisherSeriesById(Integer publisherSeriesId) {
        return ResponseEntity.ok(publisherSeriesInterface.getPublisherSeriesById(publisherSeriesId));
    }
}